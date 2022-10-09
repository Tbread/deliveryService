package com.practice.delivery.service.implemented

import com.practice.delivery.dto.request.OrderRequestDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.entity.*
import com.practice.delivery.repository.*
import com.practice.delivery.service.OrderService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Objects
import javax.servlet.http.HttpServletResponse


@Service
class OrderServiceImpl(
    private var orderRepository: OrderRepository,
    private var orderedMenuRepository: OrderedMenuRepository,
    private var menuRepository: MenuRepository,
    private var menuOptionRepository: MenuOptionRepository,
    private var couponRepository: CouponRepository
) : OrderService {

    @Transactional
    override fun order(userDetails: UserDetailsImpl, req: OrderRequestDto): DefaultResponseDto {
        var res = DefaultResponseDto()
        if (userDetails.getUser().getAuth() != "DEFAULT") {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if (req.menuList.size != req.quantityList.size) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = "주문한 메뉴와 메뉴의 주문 개수 리스트가 맞지 않습니다."
            } else {
                var menuList = arrayListOf<Menu>()
                var mainMenuSet = hashSetOf<Long>()
                var storeSet = hashSetOf<Store>()
                var necessaryMainSet = hashSetOf<Long>()
                for (menuId in req.menuList) {
                    if (!menuRepository.existsById(menuId)) {
                        //존재하지 않는 메뉴 ID가 포함됨
                        res.code = HttpServletResponse.SC_BAD_REQUEST
                        res.msg = "존재하지 않는 메뉴 ID입니다."
                        return res
                    }
                    var menu = menuRepository.findById(menuId).get()
                    if (menu.thisIsSoldOut) {
                        //품절 메뉴가 포함됨
                        res.code = HttpServletResponse.SC_BAD_REQUEST
                        res.msg = "품절 메뉴가 포함되어있습니다."
                    }
                    storeSet.add(menu.store!!)
                    if (menu.thisIsOption) {
                        necessaryMainSet.add(menuOptionRepository.findBySubMenu(menu)!!.topMenu!!.id)
                    } else {
                        mainMenuSet.add(menu.id)
                    }
                    menuList.add(menu)
                }
                if (storeSet.size != 1) {
                    // 가게가 다른 메뉴들이 포함됨
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "한번에 한개의 가게에서만 주문할 수 있습니다."
                } else {
                    for (menuId in necessaryMainSet) {
                        if (menuId !in mainMenuSet) {
                            // 옵션메뉴를 주문했으나 해당 옵션메뉴를 포함한 메뉴가 주문리스트에 없음
                            res.code = HttpServletResponse.SC_BAD_REQUEST
                            res.msg = "메뉴의 옵션만 주문할 수 없습니다."
                            return res
                        }
                    }
                    var coupon: Coupon? = null
                    if (Objects.nonNull(req.couponId)) {
                        if (couponRepository.existsById(req.couponId!!)) {
                            coupon = couponRepository.findById(req.couponId!!).get()
                            if (coupon.expired) {
                                //쿠폰이 만료됨
                                res.code = HttpServletResponse.SC_BAD_REQUEST
                                res.msg = "만료된 쿠폰입니다."
                                return res
                            } else {
                                if (!coupon.available) {
                                    //쿠폰이 이미 사용됨
                                    res.code = HttpServletResponse.SC_BAD_REQUEST
                                    res.msg = "이미 사용된 쿠폰입니다."
                                    return res
                                }
                            }
                        } else {
                            //쿠폰 ID를 입력받았으나 해당 쿠폰이 존재하지 않음
                            res.code = HttpServletResponse.SC_BAD_REQUEST
                            res.msg = "존재하지 않는 쿠폰 ID입니다."
                            return res
                        }
                    }
                    //메인 주문 로직
                    var order = Order()
                    order.orderer = userDetails.getUser()
                    var orderedMenuList = arrayListOf<OrderedMenu>()
                    var priceList = ArrayList<Int>()
                    for (i: Int in 0 until menuList.size) {
                        priceList.add(menuList[i].price * req.quantityList[i])
                        var orderedMenu = OrderedMenu()
                        orderedMenu.menu = menuList[i]
                        orderedMenu.quantity = req.quantityList[i]
                        orderedMenuList.add(orderedMenu)
                    }
                    var initialPrice: Int = priceList.sum()
                    order.initialPrice = initialPrice
                    var finalPrice: Int = 0
                    var discounted: Int = 0
                    if (Objects.nonNull(coupon)) {
                        //입력된 쿠폰이 있을경우
                        if (coupon!!.masterCoupon!!.minSpend > initialPrice) {
                            //쿠폰 최소 사용 금액보다 주문금액이 적을경우
                            res.code = HttpServletResponse.SC_OK
                            res.msg = "쿠폰의 최소 사용금액 이상 주문에만 사용할 수 있습니다."
                            return res
                        }
                        if (coupon!!.masterCoupon!!.discountPrice != 0) {
                            //절대할인형 쿠폰인경우
                            finalPrice = initialPrice - coupon.masterCoupon!!.discountPrice
                        } else {
                            //퍼센트할인형 쿠폰인경우
                            discounted = initialPrice * (coupon.masterCoupon!!.discountRate / 100)
                            if ((discounted > coupon.masterCoupon!!.maxDiscount) && coupon.masterCoupon!!.maxDiscount != 0){
                                //최대 할인 금액이 존재하고 최대 할인 금액을 초과한 할인인 경우
                                discounted = coupon.masterCoupon!!.maxDiscount
                            }
                        }
                        coupon.useCoupon()
                    }
                    finalPrice = initialPrice - discounted
                    order.finalPrice = finalPrice
                    order.usedCoupon = coupon
                    orderRepository.save(order)
                    for (orderedMenu in orderedMenuList){
                        orderedMenu.order = order
                        orderedMenuRepository.save(orderedMenu)
                    }
                    res.code = HttpServletResponse.SC_OK
                    res.msg = "성공적으로 주문하였습니다."
                }
            }
        }
        return res
    }

}