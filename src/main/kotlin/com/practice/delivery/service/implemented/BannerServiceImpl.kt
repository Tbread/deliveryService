package com.practice.delivery.service.implemented

import com.practice.delivery.dto.request.AddBannerRequestDto
import com.practice.delivery.dto.response.AddBannerResponseDto
import com.practice.delivery.entity.Banner
import com.practice.delivery.repository.BannerRepository
import com.practice.delivery.service.BannerService
import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult
import javax.servlet.http.HttpServletResponse

@Service
class BannerServiceImpl(private var bannerRepository: BannerRepository) : BannerService {

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

    override fun expireBanner(userDetails: UserDetailsImpl, id: Long): Any {
        TODO("Not yet implemented")
    }

    override fun viewBannerList(): Any {
        TODO("Not yet implemented")
    }
}