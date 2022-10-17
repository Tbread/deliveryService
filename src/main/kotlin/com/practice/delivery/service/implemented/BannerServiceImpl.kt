package com.practice.delivery.service.implemented

import com.practice.delivery.dto.request.AddBannerRequestDto
import com.practice.delivery.dto.response.AddBannerResponseDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.dto.response.ViewBannerListResponseDto
import com.practice.delivery.entity.Banner
import com.practice.delivery.model.SimpleBanner
import com.practice.delivery.repository.BannerRepository
import com.practice.delivery.repository.dslRepository.QBannerRepository
import com.practice.delivery.service.BannerService
import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult
import javax.servlet.http.HttpServletResponse

@Service
class BannerServiceImpl(
    private var bannerRepository: BannerRepository,
    private var qBannerRepository: QBannerRepository
) : BannerService {

    override fun addBanner(
        userDetails: UserDetailsImpl,
        req: AddBannerRequestDto,
        bindingResult: BindingResult
    ): AddBannerResponseDto {
        val res = AddBannerResponseDto()
        if ("ADMIN" !in userDetails.getUser().getAuthorities()) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if (bindingResult.hasErrors()) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = bindingResult.allErrors[0].defaultMessage!!
            } else {
                val banner = Banner(req.bannerImgSrc, req.bannerSrc, userDetails.getUser(), req.expireDate)
                bannerRepository.save(banner)
                res.code = HttpServletResponse.SC_OK
                res.msg = "성공적으로 배너를 저장하였습니다."
            }
        }
        return res
    }

    override fun expireBanner(userDetails: UserDetailsImpl, id: Long): DefaultResponseDto {
        val res = DefaultResponseDto()
        if ("ADMIN" !in userDetails.getUser().getAuthorities()) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if (!bannerRepository.existsById(id)) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = "존재하지 않는 배너 ID입니다."
            } else {
                val banner = bannerRepository.findById(id).get()
                banner.expireBanner()
                res.code = HttpServletResponse.SC_OK
                res.msg = "성공적으로 만료 처리하였습니다."
            }
        }
        return res
    }

    override fun viewBannerList(statusCode: Int?): ViewBannerListResponseDto {
        val res = ViewBannerListResponseDto()
        val bannerList = when (statusCode) {
            0 -> {
                qBannerRepository.getLiveBannerList()
            }
            1 -> {
                qBannerRepository.getDeadBannerList()
            }
            else -> {
                qBannerRepository.getAll()
            }
        }
        val simpleBannerList = arrayListOf<SimpleBanner>()
        for (banner in bannerList) {
            val simpleBanner = SimpleBanner(banner)
            simpleBannerList.add(simpleBanner)
        }
        res.code = HttpServletResponse.SC_OK
        res.msg = "성공적으로 불러왔습니다."
        res.simpleBannerList = simpleBannerList
        return res
    }
}