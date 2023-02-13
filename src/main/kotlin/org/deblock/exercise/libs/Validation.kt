package org.deblock.exercise.libs

import am.ik.yavi.core.ConstraintViolationsException
import am.ik.yavi.core.Validator

public fun <T : Any> Validator<T>.throwIfInvalid(target: T) {
    this.validate(target).throwIfInvalid { ConstraintViolationsException(it) }
}