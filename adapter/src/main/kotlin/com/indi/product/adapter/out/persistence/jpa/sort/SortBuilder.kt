package com.indi.product.adapter.out.persistence.jpa.sort

import com.indi.product.adapter.out.persistence.jpa.sort.criterion.SalesUnitCriterion
import com.indi.product.adapter.out.persistence.jpa.sort.criterion.SortCriterion
import com.indi.product.adapter.out.persistence.jpa.sort.criterion.StockRatioCriterion
import com.indi.product.application.model.valueobject.metric.Metric
import com.indi.product.application.model.valueobject.metric.SalesUnitMetric
import com.indi.product.application.model.valueobject.metric.StockRatioMetric
import com.indi.product.application.port.dto.Direction

class SortBuilder(private val direction: Direction) {

    private val criteria = mutableListOf<SortCriterion>()

    fun addMetric(metric: Metric) {
        if (metric.weight > 0) {
            val criterion =
                when (metric) {
                    is SalesUnitMetric -> SalesUnitCriterion(metric)
                    is StockRatioMetric -> StockRatioCriterion(metric)
                }

            criteria.add(criterion)
        }

    }

    fun buildSortStatement(): String {
        val orderField = criteria.joinToString(" + ") {
            it.generateSortStatement()
        }

        return when {
            orderField.isBlank() -> "ORDER BY id $direction"
            else -> """ORDER BY ($orderField) $direction"""
        }
    }
}