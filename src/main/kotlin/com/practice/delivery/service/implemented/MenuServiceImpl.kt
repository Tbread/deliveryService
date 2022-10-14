package com.practice.delivery.service.implemented

import com.practice.delivery.dto.request.AddMenuRequestDto
import com.practice.delivery.dto.request.UpdateMenuRequestDto
import com.practice.delivery.dto.response.AddMenuResponseDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.dto.response.ShowMenuResponseDto
import com.practice.delivery.entity.Menu
import com.practice.delivery.entity.MenuOption
import com.practice.delivery.model.OptionMenu
import com.practice.delivery.model.SimpleMenu
import com.practice.delivery.repository.MenuOptionRepository
import com.practice.delivery.repository.MenuRepository
import com.practice.delivery.repository.StoreRepository
import com.practice.delivery.repository.dslrepository.QMenuOptionRepository
import com.practice.delivery.service.MenuService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.BindingResult
import java.util.*
import javax.servlet.http.HttpServletResponse

@Service
class MenuServiceImpl(
    private var menuRepository: MenuRepository,
    private var menuOptionRepository: MenuOptionRepository,
    private var storeRepository: StoreRepository,
    private var qMenuOptionRepository: QMenuOptionRepository
) : MenuService {

    @Transactional
    override fun addMenu(
        userDetails: UserDetailsImpl,
        req: AddMenuRequestDto,
        bindingResult: BindingResult
    ): AddMenuResponseDto {
        val res = AddMenuResponseDto()
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
                        val store = storeRepository.findByOwner(userDetails.getUser())
                        val mainMenu = Menu()
                        mainMenu.menuName = req.menuName!!
                        mainMenu.desc = req.desc
                        mainMenu.imgSrc = req.imgSrc
                        mainMenu.thisHasOption = req.hasOption!!
                        mainMenu.thisIsOption = false
                        mainMenu.price = req.price!!
                        mainMenu.store = store
                        menuRepository.save(mainMenu)
                        if (mainMenu.thisHasOption){
                            for (subMenuRequest:OptionMenu in req.optionMenuList!!){
                                val subMenu = Menu()
                                val menuOption = MenuOption()
                                subMenu.menuName = subMenuRequest.name
                                subMenu.price = subMenuRequest.price
                                subMenu.thisIsOption = true
                                subMenu.store = store
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

    override fun showMenuList(id: Long): ShowMenuResponseDto {
        val res = ShowMenuResponseDto()
        if (!storeRepository.existsById(id)){
            res.code = HttpServletResponse.SC_BAD_REQUEST
            res.msg = "존재하지 않는 가게 ID입니다."
        } else {
            val menuList = menuRepository.findByStoreAndThisIsOption(storeRepository.findById(id).get(),false)
            val simpleMenuList = arrayListOf<SimpleMenu>()
            for (topMenu:Menu in menuList){
                val menuOptionList = qMenuOptionRepository.findByMainMenu(topMenu)
                val subMenuList = arrayListOf<OptionMenu>()
                for (menuOption in menuOptionList){
                    val subMenu = OptionMenu(menuOption.subMenu!!)
                    subMenuList.add(subMenu)
                }
                simpleMenuList.add(SimpleMenu(topMenu,subMenuList))
            }
            res.code = HttpServletResponse.SC_OK
            res.msg = "성공적으로 불러왔습니다."
            res.simpleMenuList = simpleMenuList
        }
        return res
    }

    @Transactional
    override fun removeMenu(userDetails: UserDetailsImpl, id: Long): DefaultResponseDto {
        val res = DefaultResponseDto()
        if ("BUSINESS" !in userDetails.getUser().getAuthorities()){
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            val selectedMenu = menuRepository.findById(id)
            if (!menuRepository.existsById(id)){
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = "존재하지 않는 메뉴 ID입니다."
            } else {
                if (selectedMenu.get().store!!.owner != userDetails.getUser()){
                    res.code = HttpServletResponse.SC_FORBIDDEN
                    res.msg = "권한이 부족합니다."
                } else {
                    if (selectedMenu.get().thisIsOption){
                        //옵션메뉴인 경우
                        val menuOption = menuOptionRepository.findBySubMenu(selectedMenu.get())
                        val topMenu = menuOption!!.topMenu
                        val menuOptionList = qMenuOptionRepository.findByMainMenu(topMenu!!)
                        if (menuOptionList.size == 1){
                            //해당메뉴 제거시 옵션메뉴가 전부 사라지는경우
                            topMenu.updateThisHasOption(false)
                        }
                        menuOptionRepository.delete(menuOption)
                    } else {
                        //메인메뉴인 경우
                        if (selectedMenu.get().thisHasOption){
                            //옵션메뉴가 존재하는경우
                            val menuOptionList = qMenuOptionRepository.findByMainMenu(selectedMenu.get())
                            for (menuOption in menuOptionList){
                                val subMenu = menuOption.subMenu
                                menuOptionRepository.delete(menuOption)
                                menuRepository.delete(subMenu!!)
                            }
                        }
                    }
                    menuRepository.delete(selectedMenu.get())
                    res.code = HttpServletResponse.SC_OK
                    res.msg = "성공적으로 삭제하였습니다."
                }
            }
        }
       return res
    }

    @Transactional
    override fun updateMenu(userDetails: UserDetailsImpl, req: UpdateMenuRequestDto, id:Long,bindingResult: BindingResult): DefaultResponseDto {
        val res = DefaultResponseDto()
        if ("BUSINESS" !in userDetails.getUser().getAuthorities()) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if (bindingResult.hasErrors()) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = bindingResult.allErrors[0].defaultMessage!!
            } else {
                if (!menuRepository.existsById(id)) {
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "존재하지 않는 메뉴 ID입니다."
                } else {
                    val menu = menuRepository.findById(id).get()
                    if (menu.store!!.owner != userDetails.getUser()) {
                        res.code = HttpServletResponse.SC_FORBIDDEN
                        res.msg = "권한이 부족합니다."
                    } else {
                        if (Objects.nonNull(req.desc)) {
                            menu.updateDesc(req.desc!!)
                        }
                        if (Objects.nonNull(req.imgSrc)) {
                            menu.updateImgSrc(req.imgSrc!!)
                        }
                        if (Objects.nonNull(req.price)) {
                            menu.updatePrice(req.price!!)
                        }
                        if (Objects.nonNull(req.menuName)) {
                            menu.updateMenuName(req.menuName!!)
                        }
                        if (Objects.nonNull(req.soldOut)) {
                            menu.updateSoldOut(req.soldOut!!)
                        }
                        res.code = HttpServletResponse.SC_OK
                        res.msg = "성공적으로 변경하였습니다."
                    }
                }
            }
        }
        return res
    }
}