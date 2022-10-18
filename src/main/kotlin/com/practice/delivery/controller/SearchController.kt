package com.practice.delivery.controller

import com.practice.delivery.dto.response.SearchResponseDto
import com.practice.delivery.service.SearchService
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("/search")
class SearchController(private var searchService: SearchService) {

    @GetMapping("/")
    fun search(
        @RequestParam(required = true, value = "words") @Valid @NotBlank words: String?,
        bindingResult: BindingResult
    ): SearchResponseDto {
        return searchService.search(words, bindingResult)
    }
}