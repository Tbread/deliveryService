package com.practice.delivery.service.implement

import com.practice.delivery.dto.request.AddMenuRequestDto
import com.practice.delivery.dto.response.AddMenuResponseDto
import com.practice.delivery.entity.Menu
import com.practice.delivery.entity.MenuOption
import com.practice.delivery.model.OptionMenu
import com.practice.delivery.model.SimpleMenu
import com.practice.delivery.repository.MenuOptionRepository
import com.practice.delivery.repository.MenuRepository
import com.practice.delivery.repository.StoreRepository
import com.practice.delivery.service.MenuService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.BindingResult
import java.util.Objects
import javax.servlet.http.HttpServletResponse

@Service
class MenuServiceImpl(
    private var menuRepository: MenuRepository,
    private var menuOptionRepository: MenuOptionRepository,
    private var storeRepository: StoreRepository
) : MenuService {

    @Transactional
    override fun addMenu(
        userDetails: UserDetailsImpl,
        req: AddMenuRequestDto,
        bindingResult: BindingResult
    ): AddMenuResponseDto {
        var res = AddMenuResponseDto()
        if (Objects.isNull(userDetails.getUser())) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if ("BUSINESS" !in userDetails.getUser().getAuthorities()) {
                res.code = HttpServletResponse.SC_FORBIDDEN
                res.msg = "권한이 부족합니다."
            } else {
                if (!storeRepository.existsByOwner(userDetails.getUser())) {
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "소유중인 가게가 존재하지 않습니다."
                } else {
                    if (bindingResult.hasErrors()){
                        res.code = HttpServletResponse.SC_BAD_REQUEST
                        res.msg = bindingResult.allErrors[0].defaultMessage
                    } else {
                        var mainMenu = Menu()
                        mainMenu.menuName = req.menuName
                        mainMenu.desc = req.desc
                        mainMenu.imgSrc = req.imgSrc
                        mainMenu.hasOption = req.hasOption
                        mainMenu.isOption = false
                        mainMenu.price = req.price
                        menuRepository.save(mainMenu)
                        if (mainMenu.hasOption){
                            for (subMenuRequest:OptionMenu in req.optionMenuList!!){
                                var subMenu = Menu()
                                var menuOption = MenuOption()
                                subMenu.menuName = subMenuRequest.name
                                subMenu.price = subMenuRequest.price
                                subMenu.isOption = true
                                menuRepository.save(subMenu)
                                menuOption.topMenu = mainMenu
                                menuOption.subMenu = subMenu
                                menuOptionRepository.save(menuOption)
                            }
                        }
                        res.code = HttpServletResponse.SC_OK
                        res.msg = "성공적으로 메뉴를 추가하였습니다."
                        res.simpleMenu = SimpleMenu(mainMenu,req.optionMenuList)
                    }
                }
            }
        }
        return res
    }
}