package com.indi.product.adapter.out.persistence.jpa.sort.criterion

import com.indi.product.application.model.valueobject.metric.StockRatioMetric

class StockRatioCriterion(
    private val metric: StockRatioMetric
) : SortCriterion {

    companion object {
        const val COLUMN_SMALL = "stock_small"
        const val COLUMN_MEDIUM = "stock_medium"
        const val COLUMN_LARGE = "stock_large"

    }

    override fun generateSortStatement() =
        """(($COLUMN_SMALL + $COLUMN_MEDIUM + $COLUMN_LARGE)/3 * ${metric.weight})"""
}