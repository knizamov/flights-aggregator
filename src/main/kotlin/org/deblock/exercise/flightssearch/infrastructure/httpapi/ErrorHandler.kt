package org.deblock.exercise.flightssearch.infrastructure.httpapi

import am.ik.yavi.core.ConstraintViolationsException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
private class ErrorHandler {

    @ResponseBody
    @ExceptionHandler(ConstraintViolationsException::class)
    fun handleConstraintViolationsException(ex: ConstraintViolationsException): ResponseEntity<*> {
        return ResponseEntity.badRequest().body(ex.violations().details())
    }
}