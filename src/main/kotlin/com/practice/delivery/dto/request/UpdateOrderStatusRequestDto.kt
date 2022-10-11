package com.practice.delivery.dto.request

import com.practice.delivery.entity.DeliveryOrder
import javax.validation.constraints.NotBlank

data class UpdateOrderStatusRequestDto (
    @field:NotBlank(message = "상태는 필수 값입니다.")
    val status:DeliveryOrder.Status
        )