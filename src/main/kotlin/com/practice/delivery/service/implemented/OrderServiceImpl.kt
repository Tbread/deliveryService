package com.practice.delivery.service.implemented

import com.practice.delivery.dto.request.OrderRequestDto
import com.practice.delivery.dto.request.UpdateOrderStatusRequestDto
import com.practice.delivery.dto.response.DefaultResponseDto
import com.practice.delivery.dto.response.ManageOrderResponseDto
import com.practice.delivery.dto.response.ViewOrderListResponseDto
import com.practice.delivery.entity.*
import com.practice.delivery.model.SimpleOrder
import com.practice.delivery.repository.*
import com.practice.delivery.repository.dslrepository.QOrderedMenuRepository
import com.practice.delivery.service.OrderService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.BindingResult
import java.util.*
import javax.servlet.http.HttpServletResponse


@Service
class OrderServiceImpl(
    private var orderRepository: OrderRepository,
    private var orderedMenuRepository: OrderedMenuRepository,
    private var menuRepository: MenuRepository,
    private var menuOptionRepository: MenuOptionRepository,
    private var couponRepository: CouponRepository,
    private var storeRepository: StoreRepository,
    private var qOrderedMenuRepository: QOrderedMenuRepository
) : OrderService {

    @Transactional
    override fun order(
        userDetails: UserDetailsImpl,
        req: OrderRequestDto,
        bindingResult: BindingResult
    ): DefaultResponseDto {
        val res = DefaultResponseDto()
        if (userDetails.getUser().getAuth() != "DEFAULT") {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if (bindingResult.hasErrors()) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = bindingResult.allErrors[0].defaultMessage!!
            } else {
                if (req.menuList.size != req.quantityList.size) {
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "주문한 메뉴와 메뉴의 주문 개수 리스트가 맞지 않습니다."
                } else {
                    val menuList = arrayListOf<Menu>()
                    val mainMenuSet = hashSetOf<Long>()
                    val storeSet = hashSetOf<Store>()
                    val necessaryMainSet = hashSetOf<Long>()
                    for (menuId in req.menuList) {
                        if (!menuRepository.existsById(menuId)) {
                            //존재하지 않는 메뉴 ID가 포함됨
                            res.code = HttpServletResponse.SC_BAD_REQUEST
                            res.msg = "존재하지 않는 메뉴 ID입니다."
                            return res
                        }
                        val menu = menuRepository.findById(menuId).get()
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
                                coupon = couponRepository.findById(req.couponId).get()
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
                        val deliveryOrder = DeliveryOrder()
                        deliveryOrder.orderer = userDetails.getUser()
                        val orderedMenuList = arrayListOf<OrderedMenu>()
                        val priceList = ArrayList<Int>()
                        for (i: Int in 0 until menuList.size) {
                            priceList.add(menuList[i].price * req.quantityList[i])
                            val orderedMenu = OrderedMenu()
                            orderedMenu.menu = menuList[i]
                            orderedMenu.quantity = req.quantityList[i]
                            orderedMenuList.add(orderedMenu)
                        }
                        val initialPrice: Int = priceList.sum()
                        deliveryOrder.initialPrice = initialPrice
                        val finalPrice: Int
                        var discounted = 0
                        if (Objects.nonNull(coupon)) {
                            //입력된 쿠폰이 있을경우
                            if (coupon!!.masterCoupon!!.minSpend > initialPrice) {
                                //쿠폰 최소 사용 금액보다 주문금액이 적을경우
                                res.code = HttpServletResponse.SC_OK
                                res.msg = "쿠폰의 최소 사용금액 이상 주문에만 사용할 수 있습니다."
                                return res
                            }
                            if (coupon.masterCoupon!!.discountPrice != 0) {
                                //절대할인형 쿠폰인경우
                                discounted = initialPrice - coupon.masterCoupon!!.discountPrice
                            } else {
                                //퍼센트할인형 쿠폰인경우
                                discounted = initialPrice * (coupon.masterCoupon!!.discountRate / 100)
                                if ((discounted > coupon.masterCoupon!!.maxDiscount) && coupon.masterCoupon!!.maxDiscount != 0) {
                                    //최대 할인 금액이 존재하고 최대 할인 금액을 초과한 할인인 경우
                                    discounted = coupon.masterCoupon!!.maxDiscount
                                }
                            }
                            coupon.useCoupon()
                        }
                        finalPrice = initialPrice - discounted
                        deliveryOrder.finalPrice = finalPrice
                        deliveryOrder.usedCoupon = coupon
                        deliveryOrder.store = orderedMenuList[0].menu!!.store
                        orderRepository.save(deliveryOrder)
                        for (orderedMenu in orderedMenuList) {
                            orderedMenu.deliveryOrder = deliveryOrder
                            orderedMenuRepository.save(orderedMenu)
                        }
                        userDetails.getUser().updateOrderDate()
                        res.code = HttpServletResponse.SC_OK
                        res.msg = "성공적으로 주문하였습니다."
                    }
                }
            }
        }
        return res
    }

    override fun viewOrderList(userDetails: UserDetailsImpl): ViewOrderListResponseDto {
        //Dto 에서 status 를 받아서 해당 status 만 불러오도록 하는게 나을지도?
        val res = ViewOrderListResponseDto()
        val simpleOrderList = arrayListOf<SimpleOrder>()
        if ("BUSINESS" !in userDetails.getUser().getAuthorities()) {
            //일반 유저 로직
            val orderList = orderRepository.findByOrderer(userDetails.getUser())
            for (order in orderList) {
                val simpleOrder = orderToSimpleOrder(order, false)
                simpleOrderList.add(simpleOrder)
            }
            res.code = HttpServletResponse.SC_OK
            res.msg = "성공적으로 불러왔습니다."
            res.simpleOrderList = simpleOrderList
        } else {
            //비즈니스 유저 로직
            if (!storeRepository.existsByOwner(userDetails.getUser())) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = "소유중인 가게가 존재하지 않습니다."
            } else {
                val orderList = orderRepository.findByStore(storeRepository.findByOwner(userDetails.getUser())!!)
                for (order in orderList) {
                    val simpleOrder = orderToSimpleOrder(order, true)
                    simpleOrderList.add(simpleOrder)
                }
                res.code = HttpServletResponse.SC_OK
                res.msg = "성공적으로 불러왔습니다."
                res.simpleOrderList = simpleOrderList
            }
        }
        return res
    }

    @Transactional
    override fun acceptOrder(userDetails: UserDetailsImpl, id: Long): ManageOrderResponseDto {
        val res = ManageOrderResponseDto()
        if ("BUSINESS" !in userDetails.getUser().getAuthorities()) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if (!storeRepository.existsByOwner(userDetails.getUser())) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = "소유중인 가게가 존재하지 않습니다."
            } else {
                if (!orderRepository.existsById(id)) {
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "존재하지 않는 주문 ID입니다."
                } else {
                    val order = orderRepository.findById(id).get()
                    if (order.store!!.owner != userDetails.getUser()) {
                        res.code = HttpServletResponse.SC_FORBIDDEN
                        res.msg = "권한이 부족합니다."
                    } else {
                        if (order.status != DeliveryOrder.Status.AWAIT) {
                            res.code = HttpServletResponse.SC_BAD_REQUEST
                            res.msg = "승낙 대기중인 주문건이 아닙니다."
                        } else {
                            order.updateStatus(DeliveryOrder.Status.COOKING)
                            res.code = HttpServletResponse.SC_OK
                            res.msg = "성공적으로 수락하였습니다."
                            res.simpleOrder = orderToSimpleOrder(order, true)
                        }
                    }
                }
            }
        }
        return res
    }

    @Transactional
    override fun denyOrder(userDetails: UserDetailsImpl, id: Long): ManageOrderResponseDto {
        val res = ManageOrderResponseDto()
        if ("BUSINESS" !in userDetails.getUser().getAuthorities()) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if (!storeRepository.existsByOwner(userDetails.getUser())) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = "소유중인 가게가 존재하지 않습니다."
            } else {
                if (!orderRepository.existsById(id)) {
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "존재하지 않는 주문 ID입니다."
                } else {
                    val order = orderRepository.findById(id).get()
                    if (order.store!!.owner != userDetails.getUser()) {
                        res.code = HttpServletResponse.SC_FORBIDDEN
                        res.msg = "권한이 부족합니다."
                    } else {
                        if (order.status != DeliveryOrder.Status.AWAIT) {
                            res.code = HttpServletResponse.SC_BAD_REQUEST
                            res.msg = "승낙 대기중인 주문건이 아닙니다."
                        } else {
                            order.updateStatus(DeliveryOrder.Status.CANCEL)
                            if (Objects.nonNull(order.usedCoupon)) {
                                //사용한 쿠폰이 있을경우
                                order.usedCoupon!!.cancelOrder()
                                //쿠폰 상태 업데이트
                            }
                            res.code = HttpServletResponse.SC_OK
                            res.msg = "성공적으로 거절하였습니다."
                            res.simpleOrder = orderToSimpleOrder(order, true)
                        }
                    }
                }
            }
        }
        return res
    }

    @Transactional
    override fun updateOrderProgress(
        userDetails: UserDetailsImpl,
        id: Long,
        req: UpdateOrderStatusRequestDto
    ): ManageOrderResponseDto {
        val res = ManageOrderResponseDto()
        if ("BUSINESS" !in userDetails.getUser().getAuthorities() || "ADMIN" !in userDetails.getUser()
                .getAuthorities()
        ) {
            res.code = HttpServletResponse.SC_FORBIDDEN
            res.msg = "권한이 부족합니다."
        } else {
            if ("ADMIN" !in userDetails.getUser()
                    .getAuthorities() && !storeRepository.existsByOwner(userDetails.getUser())
            ) {
                res.code = HttpServletResponse.SC_BAD_REQUEST
                res.msg = "소유중인 가게가 존재하지 않습니다."
            } else {
                if (!orderRepository.existsById(id)) {
                    res.code = HttpServletResponse.SC_BAD_REQUEST
                    res.msg = "존재하지 않는 주문 ID입니다."
                } else {
                    val order = orderRepository.findById(id).get()
                    if ("ADMIN" !in userDetails.getUser()
                            .getAuthorities() && order.store!!.owner != userDetails.getUser()
                    ) {
                        res.code = HttpServletResponse.SC_FORBIDDEN
                        res.msg = "권한이 부족합니다."
                    } else {
                        if (order.status == DeliveryOrder.Status.CANCEL) {
                            //현재 주문상태가 취소상태인 주문을 변경하는경우
                            if ("ADMIN" !in userDetails.getUser().getAuthorities()) {
                                //관리자가 아니라면
                                res.code = HttpServletResponse.SC_FORBIDDEN
                                res.msg = "권한이 부족합니다."
                            }
                        }
                        order.updateStatus(req.status)
                        if (req.status == DeliveryOrder.Status.CANCEL) {
                            //주문 취소 요청건인경우
                            if (Objects.nonNull(order.usedCoupon)) {
                                //적용된 쿠폰이 있다면
                                order.usedCoupon!!.cancelOrder()
                            }
                        }
                        res.code = HttpServletResponse.SC_OK
                        res.msg = "성공적으로 업데이트하였습니다."
                        res.simpleOrder = orderToSimpleOrder(order, true)
                    }
                }
            }
        }
        return res
    }

    fun orderToSimpleOrder(deliveryOrder: DeliveryOrder, isBusiness: Boolean): SimpleOrder {
        val orderedMenuList = qOrderedMenuRepository.findByOrder(deliveryOrder)
        val menuNameList = arrayListOf<String>()
        val quantityList = arrayListOf<Int>()
        for (orderedMenu in orderedMenuList) {
            menuNameList.add(orderedMenu.menu!!.menuName)
            quantityList.add(orderedMenu.quantity)
        }
        val simpleOrder = SimpleOrder()
        simpleOrder.orderId = deliveryOrder.id
        simpleOrder.menuNameList = menuNameList
        simpleOrder.quantityList = quantityList
        simpleOrder.status = deliveryOrder.status
        if (isBusiness) {
            simpleOrder.priceSum = deliveryOrder.initialPrice
        } else {
            simpleOrder.priceSum = deliveryOrder.finalPrice
        }
        return simpleOrder
    }


}