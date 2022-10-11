package com.practice.delivery.utils.validator

import com.practice.delivery.utils.validator.ValidCustomEnum
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class EnumValidator : ConstraintValidator<ValidCustomEnum, CharSequence> {
    private val acceptedValues: MutableList<String> = mutableListOf()

    override fun initialize(constraintAnnotation: ValidCustomEnum) {
        super.initialize(constraintAnnotation)
        acceptedValues.addAll(constraintAnnotation.enumClass.java.enumConstants.map { it.name })
    }

    override fun isValid(value: CharSequence?, context: ConstraintValidatorContext?): Boolean {
        return if (value == null) {
            true
        } else acceptedValues.contains(value.toString())
    }
}