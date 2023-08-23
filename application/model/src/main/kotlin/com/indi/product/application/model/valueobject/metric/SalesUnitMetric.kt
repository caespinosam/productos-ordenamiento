package com.indi.product.application.model.valueobject.metric

data class SalesUnitMetric(override val weight: Double, override val name: String = "salesUnit") : Metric