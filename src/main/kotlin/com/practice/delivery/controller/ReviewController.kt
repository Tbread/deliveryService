package com.practice.delivery.controller

import com.practice.delivery.dto.request.WriteReviewRequestDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.dto.response.ViewReviewListResponseDto
import com.practice.delivery.dto.response.WriteReviewResponseDto
import com.practice.delivery.service.ReviewService
import com.practice.delivery.service.implemented.UserDetailsImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Tag(name = "리뷰 관리")
@RestController
@RequestMapping("/review")
class ReviewController(private var reviewService: ReviewService) {

    @Operation(summary = "리뷰 작성 API", description = "헤더에 Authorization 으로 JWT 토큰을 요구합니다.")
    @PostMapping("/write")
    fun writeReview(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @RequestBody @Valid req: WriteReviewRequestDto,
        bindingResult: BindingResult
    ): WriteReviewResponseDto {
        return reviewService.writeReview(userDetails, req, bindingResult)
    }

    @Operation(summary = "리뷰 삭제 API", description = "헤더에 Authorization 으로 JWT 토큰을 요구합니다.")
    @DeleteMapping("/delete/{id}")
    fun deleteReview(
        @Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl,
        @PathVariable id: Long
    ): DefaultResponseDto {
        return reviewService.deleteReview(userDetails, id)
    }

    @Operation(
        summary = "리뷰 리스트 조회 API",
        description = "일반 유저라면 자신이 작성한 리뷰 리스트를, 사업자 유저라면 자신의 가게에 작성된 리뷰 리스트를 불러옵니다.\n 헤더에 Authorization 으로 JWT 토큰을 요구합니다."
    )
    @GetMapping("/view")
    fun viewReviewList(@Parameter(hidden = true) @AuthenticationPrincipal userDetails: UserDetailsImpl): ViewReviewListResponseDto {
        return reviewService.viewReviewList(userDetails)
    }
}