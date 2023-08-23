package com.indi.product.adapter.out.persistence.jpa

import com.indi.product.application.model.valueobject.ProductId
import com.indi.product.application.model.valueobject.metric.SalesUnitMetric
import com.indi.product.application.model.valueobject.metric.StockRatioMetric
import com.indi.product.application.port.dto.Direction
import com.indi.product.application.port.dto.PageRequest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class SearchProductsJPARepositoryIntegrationTest {

    @Autowired
    lateinit var searchProductsJPARepository: SearchProductsJPARepository

    /*
        Current data in the in-memory DB is (see src/test/resources):

        id name                      sales_units stock
        1 V-NECH BASIC SHIRT         100         S: 4 / M:9 / L:0
        2 CONTRASTING FABRIC T-SHIRT 50          S: 35 / M:9 / L:9
        3 RAISED PRINT T-SHIRT       80          S: 20 / M:2 / L:20
        4 PLEATED T-SHIRT            3           S: 25  / M:30 / L:10
        5 CONTRASTING LACE T-SHIRT   650         S: 0 / M:1 / L:0
        6 SLOGAN T-SHIRT             20          S: 9  / M:2 / L:5
     */

    @Nested
    inner class WhenSearchProducts {

        @Test
        fun should_return_items_ordered_by_id_given_no_metric() {
            val products = searchProductsJPARepository.searchProducts(
                listOf(),
                PageRequest(
                    0,
                    20,
                    Direction.DESC
                )
            ).content

            Assertions.assertThat(products).extracting("id").containsAll(
                listOf(
                    ProductId(6),
                    ProductId(5),
                    ProductId(4),
                    ProductId(3),
                    ProductId(2),
                    ProductId(1)
                )
            )
        }

        @Nested
        inner class WhenOnlyOneMetric {
            @Test
            fun should_return_all_items_ordered_by_sales_unit_descending() {
                val products = searchProductsJPARepository.searchProducts(
                    listOf(SalesUnitMetric(1.0)),
                    PageRequest(
                        0,
                        20,
                        Direction.DESC
                    )
                ).content

                Assertions.assertThat(products).extracting("id").containsAll(
                    listOf(
                        ProductId(5),
                        ProductId(1),
                        ProductId(3),
                        ProductId(2),
                        ProductId(6),
                        ProductId(4)
                    )
                )
            }

            @Test
            fun should_return_all_items_ordered_by_sales_unit_ascending() {
                val products = searchProductsJPARepository.searchProducts(
                    listOf(SalesUnitMetric(1.0)),
                    PageRequest(
                        0,
                        20,
                        Direction.ASC
                    )
                ).content

                Assertions.assertThat(products).extracting("id").containsAll(
                    listOf(
                        ProductId(4),
                        ProductId(6),
                        ProductId(2),
                        ProductId(3),
                        ProductId(1),
                        ProductId(5)
                    )
                )
            }

            @Test
            fun should_return_all_items_ordered_by_stock_ratio_descending() {
                val products = searchProductsJPARepository.searchProducts(
                    listOf(StockRatioMetric(1.0)),
                    PageRequest(
                        0,
                        20,
                        Direction.DESC
                    )
                ).content

                Assertions.assertThat(products).extracting("id").containsAll(
                    listOf(
                        ProductId(4),
                        ProductId(2),
                        ProductId(3),
                        ProductId(6),
                        ProductId(1),
                        ProductId(5)
                    )
                )
            }

            @Test
            fun should_return_all_items_ordered_by_stock_ratio_ascending() {
                val products = searchProductsJPARepository.searchProducts(
                    listOf(StockRatioMetric(1.0)),
                    PageRequest(
                        0,
                        20,
                        Direction.ASC
                    )
                ).content

                Assertions.assertThat(products).extracting("id").containsAll(
                    listOf(
                        ProductId(5),
                        ProductId(1),
                        ProductId(6),
                        ProductId(3),
                        ProductId(2),
                        ProductId(4)
                    )
                )
            }
        }

        @Nested
        inner class WhenMultipleMetrics {
            @Test
            fun should_return_all_items_ordered_by_sales_unit_20_and_stock_ratio_80_descending() {
                val products = searchProductsJPARepository.searchProducts(
                    listOf(
                        SalesUnitMetric(0.2),
                        StockRatioMetric(0.8),
                    ),
                    PageRequest(
                        0,
                        20,
                        Direction.DESC
                    )
                ).content

                Assertions.assertThat(products).extracting("id").containsAll(
                    listOf(
                        ProductId(5),
                        ProductId(3),
                        ProductId(2),
                        ProductId(1),
                        ProductId(4),
                        ProductId(6)
                    )
                )
            }

            @Test
            fun should_return_all_items_ordered_by_sales_unit_80_and_stock_ratio_20_descending() {
                val products = searchProductsJPARepository.searchProducts(
                    listOf(
                        SalesUnitMetric(0.2),
                        StockRatioMetric(0.8),
                    ),
                    PageRequest(
                        0,
                        20,
                        Direction.DESC
                    )
                ).content

                Assertions.assertThat(products).extracting("id").containsAll(
                    listOf(
                        ProductId(5),
                        ProductId(1),
                        ProductId(3),
                        ProductId(2),
                        ProductId(6),
                        ProductId(4)
                    )
                )
            }
        }

        @Nested
        inner class ValidatePagination {
            @Test
            fun should_return_only_2_elements_per_page() {
                val page0 = searchProductsJPARepository.searchProducts(
                    listOf(SalesUnitMetric(1.0)),
                    PageRequest(
                        pageNumber = 0,
                        pageSize = 2,
                        direction = Direction.DESC
                    )
                )

                with(page0) {
                    Assertions.assertThat(currentPage).isEqualTo(0)
                    Assertions.assertThat(maxElementsPerPage).isEqualTo(2)
                    Assertions.assertThat(currentElements).isEqualTo(2)
                    Assertions.assertThat(totalElements).isEqualTo(6)
                }
                Assertions.assertThat(page0.content).extracting("id").containsAll(
                    listOf(
                        ProductId(5),
                        ProductId(1),
                    )
                )

                val page1 = searchProductsJPARepository.searchProducts(
                    listOf(SalesUnitMetric(1.0)),
                    PageRequest(
                        pageNumber = 1,
                        pageSize = 2,
                        direction = Direction.DESC
                    )
                )

                with(page1) {
                    Assertions.assertThat(currentPage).isEqualTo(1)
                    Assertions.assertThat(maxElementsPerPage).isEqualTo(2)
                    Assertions.assertThat(currentElements).isEqualTo(2)
                    Assertions.assertThat(totalElements).isEqualTo(6)
                }
                Assertions.assertThat(page1.content).extracting("id").containsAll(
                    listOf(
                        ProductId(3),
                        ProductId(2),
                    )
                )

                val page2 = searchProductsJPARepository.searchProducts(
                    listOf(SalesUnitMetric(1.0)),
                    PageRequest(
                        pageNumber = 2,
                        pageSize = 2,
                        direction = Direction.DESC
                    )
                )

                with(page2) {
                    Assertions.assertThat(currentPage).isEqualTo(2)
                    Assertions.assertThat(maxElementsPerPage).isEqualTo(2)
                    Assertions.assertThat(currentElements).isEqualTo(2)
                    Assertions.assertThat(totalElements).isEqualTo(6)
                }
                Assertions.assertThat(page2.content).extracting("id").containsAll(
                    listOf(
                        ProductId(6),
                        ProductId(4)
                    )
                )
            }
        }
    }
}