package com.practice.delivery.service.implemented

import com.practice.delivery.dto.request.CreateCouponRequestDto
import com.practice.delivery.dto.response.CreateCouponResponseDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.entity.MasterCoupon
import com.practice.delivery.model.SimpleCoupon
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
    couponRepository: CouponRepository
) : CouponService {

    @Transactional
    override fun createCoupon(
        userDetails: UserDetailsImpl,
        req: CreateCouponRequestDto,
        bindingResult: BindingResult
    ): CreateCouponResponseDto {
        var res = CreateCouponResponseDto()
        if ("ADMIN" !in userDetails.getUser().getAuthorities()){
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if (bindingResult.hasErrors()){
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = bindingResult.allErrors[0].defaultMessage!!
            } else {
                var coupon = MasterCoupon(req.discountRate,req.discountPrice,req.minSpend,req.maxDiscount,userDetails.getUser(),req.expiryDate,req.quantity)
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
        TODO("Not yet implemented")
    }

}