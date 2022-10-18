package com.practice.delivery.controller

import com.practice.delivery.dto.response.SearchResponseDto
import com.practice.delivery.service.SearchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Tag(name = "검색")
@RestController
@RequestMapping("/search")
class SearchController(private var searchService: SearchService) {

    @Operation(summary = "파라미터를 받아 해당 파라미터의 단어를 메뉴, 또는 가게 이름에서 검색하여 일치하는 결과가 존재하는 가게를 리턴합니다")
    @GetMapping("/")
    fun search(
        @RequestParam(required = true, value = "words") @Valid @NotBlank words: String?,
        bindingResult: BindingResult
    ): SearchResponseDto {
        return searchService.search(words, bindingResult)
    }
}