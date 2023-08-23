package com.indi.product.adapter.out.persistence.jpa.sort.criterion

sealed interface SortCriterion {
    fun generateSortStatement(): String
}