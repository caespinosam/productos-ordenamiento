package com.indi.product.adapter.out.persistence.jpa.datamodel

import com.indi.product.application.model.entity.Product
import com.indi.product.application.model.valueobject.ProductId
import com.indi.product.application.model.valueobject.Stock

data class ProductDataModel(
    val id: Int,
    val name: String,
    val salesUnit: Int,
    val stockSmall: Int,
    val stockMedium: Int,
    val stockLarge: Int,
) {

    fun toProduct() =
        Product(
            id = ProductId(id),
            name = name,
            salesUnit = salesUnit,
            stock = Stock(stockSmall, stockMedium, stockLarge)
        )
}

