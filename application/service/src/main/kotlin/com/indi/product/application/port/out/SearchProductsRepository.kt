package com.indi.product.application.port.out

import com.indi.product.application.model.entity.Product
import com.indi.product.application.model.valueobject.metric.Metric
import com.indi.product.application.port.dto.Page
import com.indi.product.application.port.dto.PageRequest


interface SearchProductsRepository {
    fun searchProducts(metrics: List<Metric>, pageRequest: PageRequest): Page<Product>
}