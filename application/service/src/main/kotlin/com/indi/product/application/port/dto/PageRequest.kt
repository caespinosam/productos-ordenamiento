package com.indi.product.application.port.dto

data class PageRequest(
    val pageNumber: Int,
    val pageSize: Int,
    val direction: Direction
)

enum class Direction {
    ASC, DESC
}