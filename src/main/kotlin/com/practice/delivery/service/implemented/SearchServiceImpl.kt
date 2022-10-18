package com.practice.delivery.service.implemented

import com.practice.delivery.dto.response.SearchResponseDto
import com.practice.delivery.model.SimpleStore
import com.practice.delivery.repository.dslRepository.QMenuRepository
import com.practice.delivery.repository.dslRepository.QStoreRepository
import com.practice.delivery.service.SearchService
import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult
import javax.servlet.http.HttpServletResponse

@Service
class SearchServiceImpl(private var qMenuRepository: QMenuRepository, private var qStoreRepository: QStoreRepository) :
    SearchService {

    override fun search(words: String?, bindingResult: BindingResult): SearchResponseDto {
        val res = SearchResponseDto()
        if (bindingResult.hasErrors()) {
            res.code = HttpServletResponse.SC_BAD_REQUEST
            res.msg = bindingResult.allErrors[0].defaultMessage!!
        } else {
            val simpleStoreList = arrayListOf<SimpleStore>()
            val menuList = qMenuRepository.searchWords(words!!)
            val storeList = qStoreRepository.searchWords(words)
            for (menu in menuList) {
                if (menu.store !in storeList) {
                    simpleStoreList.add(SimpleStore(menu.store!!))
                }
            }
            for (store in storeList) {
                simpleStoreList.add(SimpleStore(store))
            }
            res.code = HttpServletResponse.SC_OK
            res.msg = "성공적으로 불러왔습니다."
            res.simpleStoreList = simpleStoreList
        }
        return res
    }

}