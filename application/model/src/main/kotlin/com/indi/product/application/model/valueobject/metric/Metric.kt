package com.indi.product.application.model.valueobject.metric

sealed interface Metric {
    val name: String
    val weight: Double
}
