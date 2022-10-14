package com.practice.delivery.service

import com.practice.delivery.dto.request.WriteReviewRequestDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.dto.response.ViewReviewListResponseDto
import com.practice.delivery.dto.response.WriteReviewResponseDto
import com.practice.delivery.service.implemented.UserDetailsImpl
import org.springframework.validation.BindingResult

interface ReviewService {

    fun writeReview(userDetails: UserDetailsImpl,req: WriteReviewRequestDto,bindingResult: BindingResult):WriteReviewResponseDto

    fun deleteReview(userDetails: UserDetailsImpl,id:Long):DefaultResponseDto

    fun viewReviewList(userDetails: UserDetailsImpl):ViewReviewListResponseDto
}