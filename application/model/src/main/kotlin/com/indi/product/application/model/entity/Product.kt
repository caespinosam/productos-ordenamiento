package com.indi.product.application.model.entity

import com.indi.product.application.model.valueobject.ProductId
import com.indi.product.application.model.valueobject.Stock

class Product(
    id: ProductId,
    val name: String,
    val salesUnit: Int,
    val stock: Stock,
) : AggregateRoot<ProductId>(id)