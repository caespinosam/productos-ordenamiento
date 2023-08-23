package com.indi.product.adapter.out.persistence.jpa.sort.criterion

import com.indi.product.application.model.valueobject.metric.SalesUnitMetric

data class SalesUnitCriterion(
    private val metric: SalesUnitMetric
) : SortCriterion {

    companion object {
        const val COLUMN_NAME = "sales_unit"
    }

    override fun generateSortStatement()= """($COLUMN_NAME * ${metric.weight})"""
}
