package com.indi.product.application.service

import com.indi.product.application.model.entity.Product
import com.indi.product.application.model.valueobject.metric.Metric
import com.indi.product.application.port.dto.Page
import com.indi.product.application.port.dto.PageRequest
import com.indi.product.application.port.`in`.SearchProductsUseCase
import com.indi.product.application.port.out.SearchProductsRepository

class SearchProductsService(
    private val searchProductsRepository: SearchProductsRepository
) : SearchProductsUseCase {
    override fun searchProducts(metrics: List<Metric>, pageRequest: PageRequest): Page<Product> {
        return searchProductsRepository.searchProducts(metrics, pageRequest)
    }
}