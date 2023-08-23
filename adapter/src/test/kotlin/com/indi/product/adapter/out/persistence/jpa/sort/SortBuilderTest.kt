package com.indi.product.adapter.out.persistence.jpa.sort

import com.indi.product.application.model.valueobject.metric.SalesUnitMetric
import com.indi.product.application.model.valueobject.metric.StockRatioMetric
import com.indi.product.application.port.dto.Direction
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SortBuilderTest {

    @Nested
    inner class WhenBuildSortStatement {

        @Test
        fun should_ignore_metrics_given_weight_is_zero() {
            val builder = SortBuilder(Direction.DESC)
            builder.addMetric(SalesUnitMetric(0.0))
            Assertions.assertThat(builder.buildSortStatement()).isEmpty()
        }

        @Test
        fun should_include_the_metric_given_weight_is_greather_than_zero() {
            val builder = SortBuilder(Direction.DESC)
            builder.addMetric(SalesUnitMetric(0.1))
            Assertions.assertThat(builder.buildSortStatement()).isEqualTo("ORDER BY ((sales_unit * 0.1)) DESC")
        }

        @Test
        fun should_include_multiple_metrics_given_weights_are_greather_than_zero() {
            val builder = SortBuilder(Direction.DESC)
            builder.addMetric(SalesUnitMetric(0.1))
            builder.addMetric(StockRatioMetric(0.5))
            Assertions.assertThat(builder.buildSortStatement()).isEqualTo(
                """ORDER BY ((sales_unit * 0.1) + ((stock_small + stock_medium + stock_large)/3 * 0.5)) DESC"""
            )
        }

        @Test
        fun should_include_the_requested_direction() {
            val builderAsc = SortBuilder(Direction.ASC)
            builderAsc.addMetric(SalesUnitMetric(0.1))
            Assertions.assertThat(builderAsc.buildSortStatement()).isEqualTo("ORDER BY ((sales_unit * 0.1)) ASC")

            val builderDesc = SortBuilder(Direction.DESC)
            builderDesc.addMetric(SalesUnitMetric(0.1))
            Assertions.assertThat(builderDesc.buildSortStatement()).isEqualTo("ORDER BY ((sales_unit * 0.1)) DESC")
        }
    }
}