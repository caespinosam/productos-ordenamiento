package com.indi.product.adapter.`in`.rest.webmodel

import com.indi.product.application.model.entity.Product
import com.indi.product.application.model.valueobject.Stock

data class ProductWebModel(
    val id: Int,
    val name: String,
    val salesUnit: Int,
    val stock: StockWebModel,
) {
    companion object {
        fun of(p: Product) = ProductWebModel(p.id.id, p.name, p.salesUnit, StockWebModel.of(p.stock))
    }
}

data class StockWebModel(
    val small: Int,
    val medium: Int,
    val large: Int
) {
    companion object {
        fun of(s: Stock) = StockWebModel(s.small, s.medium, s.large)
    }
}