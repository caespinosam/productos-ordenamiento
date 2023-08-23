package com.indi.product.adapter.`in`.rest.errorHandling

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler
    protected fun handle(e: ConstraintViolationException): ResponseEntity<ErrorMessageModel> {
        val message = ErrorMessageModel(
            HttpStatus.BAD_REQUEST,
            e.message,
        )

        return ResponseEntity(message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler
    protected fun handle(e: Exception): ResponseEntity<ErrorMessageModel> {
        val message = ErrorMessageModel(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal server error",
        )

        return ResponseEntity(message, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}