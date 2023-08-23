package com.indi.product.application.model.valueobject

data class ProductId(override val id: Int) : BaseId<Int>(id)
