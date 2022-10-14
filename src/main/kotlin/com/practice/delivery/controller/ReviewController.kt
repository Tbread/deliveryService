package com.practice.delivery.controller

import com.practice.delivery.dto.request.WriteReviewRequestDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.dto.response.ViewReviewListResponseDto
import com.practice.delivery.dto.response.WriteReviewResponseDto
import com.practice.delivery.service.ReviewService
import com.practice.delivery.service.implemented.UserDetailsImpl
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/review")
class ReviewController(private var reviewService: ReviewService) {

    @PostMapping("/write")
    fun writeReview(@AuthenticationPrincipal userDetails:UserDetailsImpl,@RequestBody @Valid req: WriteReviewRequestDto,bindingResult: BindingResult):WriteReviewResponseDto{
        return reviewService.writeReview(userDetails,req,bindingResult)
    }

    @DeleteMapping("/delete/{id}")
    fun deleteReview(@AuthenticationPrincipal userDetails: UserDetailsImpl,@PathVariable id:Long):DefaultResponseDto{
        return reviewService.deleteReview(userDetails,id)
    }

    @GetMapping("/view")
    fun viewReviewList(@AuthenticationPrincipal userDetails: UserDetailsImpl):ViewReviewListResponseDto{
        return reviewService.viewReviewList(userDetails)
    }
}