package com.indi.product.application.model.valueobject.metric

data class StockRatioMetric(override val weight: Double, override val name: String = "stockRatio") : Metric