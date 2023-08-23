package com.indi.product.adapter.`in`.rest

import com.indi.product.adapter.`in`.rest.webmodel.ProductWebModel
import com.indi.product.application.model.valueobject.metric.SalesUnitMetric
import com.indi.product.application.model.valueobject.metric.StockRatioMetric
import com.indi.product.application.port.dto.Direction
import com.indi.product.application.port.dto.Page
import com.indi.product.application.port.dto.PageRequest
import com.indi.product.application.port.`in`.SearchProductsUseCase
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@Validated
class SearchProductsController(
    private val searchProductsUseCase: SearchProductsUseCase
) {
    @GetMapping("/products", produces = ["application/json"])
    fun searchProducts(
        @RequestParam("sales_unit", defaultValue = "0")
        @DecimalMax(value = "1.0")
        @PositiveOrZero
        salesUnit: BigDecimal,

        @RequestParam("stock_ratio", defaultValue = "0")
        @DecimalMax(value = "1.0")
        @PositiveOrZero
        stockRatio: BigDecimal,

        @RequestParam("page_number", defaultValue = "0")
        @PositiveOrZero
        pageNumber: Int,

        @RequestParam("page_size", defaultValue = "20")
        @PositiveOrZero
        pageSize: Int,

        @RequestParam("direction", defaultValue = "DESC")
        direction: Direction
    ): Page<ProductWebModel> {
        val metrics = listOf(SalesUnitMetric(salesUnit.toDouble()), StockRatioMetric(stockRatio.toDouble()))
        searchProductsUseCase.searchProducts(metrics, PageRequest(pageNumber, pageSize, direction))

        val productsPage = searchProductsUseCase
            .searchProducts(metrics, PageRequest(pageNumber, pageSize, direction))
        return Page(
            content = productsPage
                .content.map { ProductWebModel.of(it) },
            metrics = productsPage.metrics,
            direction = productsPage.direction,
            currentPage = productsPage.currentPage,
            currentElements = productsPage.currentElements,
            maxElementsPerPage = productsPage.maxElementsPerPage,
            totalElements = productsPage.totalElements
        )
    }
}