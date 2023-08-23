package com.indi.product.application.model.entity

abstract class AggregateRoot<ID>(override val id: ID) : BaseEntity<ID>(id)