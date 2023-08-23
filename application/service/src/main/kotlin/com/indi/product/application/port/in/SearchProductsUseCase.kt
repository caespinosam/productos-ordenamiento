package com.indi.product.application.port.`in`

import com.indi.product.application.model.entity.Product
import com.indi.product.application.model.valueobject.metric.Metric
import com.indi.product.application.port.dto.PageRequest
import com.indi.product.application.port.dto.Page


interface SearchProductsUseCase {
    fun searchProducts(metrics: List<Metric>, pageRequest: PageRequest): Page<Product>
}