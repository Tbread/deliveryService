package com.practice.delivery.utils.validator

import com.practice.delivery.utils.validator.EnumValidator
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [EnumValidator::class])
@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidCustomEnum(
    val enumClass: KClass<out Enum<*>>,
    val message:String = "",
    val groups:Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)