package com.indi.product.adapter.`in`.rest.errorHandling

import org.springframework.http.HttpStatus

class ErrorMessageModel(
    var status: HttpStatus,
    var message: String? = null
)