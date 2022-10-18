package com.practice.delivery.service

import com.practice.delivery.dto.response.SearchResponseDto
import org.springframework.validation.BindingResult

interface SearchService {

    fun search(words: String?, bindingResult: BindingResult): SearchResponseDto

}