package com.practice.delivery.service.implemented

import com.practice.delivery.dto.request.CreateCouponRequestDto
import com.practice.delivery.dto.response.CreateCouponResponseDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.entity.Coupon
import com.practice.delivery.entity.MasterCoupon
import com.practice.delivery.model.SimpleCoupon
import com.practice.delivery.repository.CouponIssuanceLogRepository
import com.practice.delivery.repository.CouponRepository
import com.practice.delivery.repository.MasterCouponRepository
import com.practice.delivery.service.CouponService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.BindingResult
import javax.servlet.http.HttpServletResponse


@Service
class CouponServiceImpl(
    private var masterCouponRepository: MasterCouponRepository,
    private var couponRepository: CouponRepository,
    private var couponIssuanceLogRepository: CouponIssuanceLogRepository
) : CouponService {

    @Transactional
    override fun createCoupon(
        userDetails: UserDetailsImpl,
        req: CreateCouponRequestDto,
        bindingResult: BindingResult
    ): CreateCouponResponseDto {
        val res = CreateCouponResponseDto()
        if ("ADMIN" !in userDetails.getUser().getAuthorities()) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if (bindingResult.hasErrors()) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = bindingResult.allErrors[0].defaultMessage!!
            } else {
                val coupon = MasterCoupon(
                    req.discountRate,
                    req.discountPrice,
                    req.minSpend,
                    req.maxDiscount,
                    userDetails.getUser(),
                    req.expiryDate,
                    req.quantity
                )
                masterCouponRepository.save(coupon)
                res.code = HttpServletResponse.SC_OK
                res.msg = "성공적으로 쿠폰을 발급하였습니다."
                res.simpleCoupon = SimpleCoupon(coupon)
            }
        }
        return res
    }

    @Transactional
    override fun couponIssuance(userDetails: UserDetailsImpl, id: Long): DefaultResponseDto {
        val res = DefaultResponseDto()
        if ("DEFAULT" != userDetails.getUser().getAuth()) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if (!couponRepository.existsById(id)) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = "존재하지 않는 쿠폰 ID입니다."
            } else {
                val masterCoupon = masterCouponRepository.findById(id).get()
                if (couponIssuanceLogRepository.existsByUserAndMasterCoupon(
                        userDetails.getUser(),
                        masterCoupon
                    )
                ) {
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "이미 발급 받은 쿠폰입니다."
                } else {
                    val coupon = Coupon(masterCoupon,userDetails.getUser())
                    couponRepository.save(coupon)
                    masterCoupon.issuanceCoupon()
                    res.code = HttpServletResponse.SC_OK
                    res.msg = "성공적으로 쿠폰을 발급받았습니다."
                }
            }
        }
        return res
    }

}