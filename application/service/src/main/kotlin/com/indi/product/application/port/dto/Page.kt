package com.indi.product.application.port.dto

import com.indi.product.application.model.valueobject.metric.Metric

data class Page<T>(
    val content: List<T>,
    val metrics: List<Metric>,
    val direction: Direction,
    val currentPage: Int,
    val currentElements: Int,
    val maxElementsPerPage: Int,
    val totalElements: Long
)

