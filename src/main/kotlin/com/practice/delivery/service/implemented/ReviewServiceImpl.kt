package com.practice.delivery.service.implemented

import com.practice.delivery.dto.request.WriteReviewRequestDto
import com.practice.delivery.dto.response.WriteReviewResponseDto
import com.practice.delivery.entity.Review
import com.practice.delivery.model.SimpleReview
import com.practice.delivery.repository.OrderRepository
import com.practice.delivery.repository.ReviewRepository
import com.practice.delivery.repository.dslrepository.QReviewRepository
import com.practice.delivery.service.ReviewService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.BindingResult
import javax.servlet.http.HttpServletResponse

@Service
class ReviewServiceImpl(
    private var reviewRepository: ReviewRepository,
    private var qReviewRepository: QReviewRepository,
    private var orderRepository: OrderRepository
) : ReviewService {

    @Transactional
    override fun writeReview(
        userDetails: UserDetailsImpl,
        req: WriteReviewRequestDto, bindingResult: BindingResult
    ): WriteReviewResponseDto {
        var res = WriteReviewResponseDto()
        if (!orderRepository.existsById(req.orderId)){
            res.code = HttpServletResponse.SC_BAD_REQUEST
            res.msg = "존재하지 않는 주문 ID입니다."
        } else {
            var order = orderRepository.findById(req.orderId).get()
            if (userDetails.getUser() != order.orderer){
                res.code = HttpServletResponse.SC_FORBIDDEN
                res.msg = "권한이 부족합니다."
            } else {
                if (order.reviewed){
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "이미 리뷰가 작성된 주문건입니다."
                } else {
                    var review = Review(userDetails.getUser(),order,req.content,req.imgSrc,req.score)
                    reviewRepository.save(review)
                    res.code = HttpServletResponse.SC_OK
                    res.msg = "성공적으로 리뷰를 작성하였습니다."
                    res.simpleReview = SimpleReview(review)
                }
            }
        }
        return res
    }
}