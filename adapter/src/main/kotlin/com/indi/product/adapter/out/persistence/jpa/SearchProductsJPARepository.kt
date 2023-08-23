package com.indi.product.adapter.out.persistence.jpa

import com.indi.product.adapter.out.persistence.jpa.datamodel.ProductDataModel
import com.indi.product.adapter.out.persistence.jpa.sort.SortBuilder
import com.indi.product.application.model.entity.Product
import com.indi.product.application.model.valueobject.metric.Metric
import com.indi.product.application.port.dto.Direction
import com.indi.product.application.port.dto.Page
import com.indi.product.application.port.dto.PageRequest
import com.indi.product.application.port.out.SearchProductsRepository
import jakarta.persistence.EntityManager
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable


open class SearchProductsJPARepository(
    private val entityManager: EntityManager
) : SearchProductsRepository {

    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ROOT_QUERY = """ 
            SELECT 
                id,
                name,
                sales_unit,
                stock_small,
                stock_medium,
                stock_large
            FROM PRODUCTS
        """

        const val COUNT_QUERY = """ 
            SELECT 
                count(1)
            FROM PRODUCTS
        """
    }


    @Cacheable(cacheNames = ["products_search"])
    override fun searchProducts(metrics: List<Metric>, pageRequest: PageRequest): Page<Product> {
        logger.debug("Searching products by hitting the DB: [metrics={}, pagination+{}]", metrics, pageRequest)

        val sortBuilder = buildSortBuilder(metrics, pageRequest.direction)
        val rawResponse = executeQuery(sortBuilder, pageRequest)
        val products = mapRawResponseToDataModel(rawResponse).map(ProductDataModel::toProduct)
        val totalItemsInDB = calculateTotalElements()

        return Page(
            content = products,
            metrics = metrics,
            direction = pageRequest.direction,
            currentPage = pageRequest.pageNumber,
            currentElements = products.size,
            maxElementsPerPage = pageRequest.pageSize,
            totalElements = totalItemsInDB
        )
    }

    private fun buildSortBuilder(metrics: List<Metric>, direction: Direction): SortBuilder {
        val sortBuilder = SortBuilder(direction)
        metrics.forEach { sortBuilder.addMetric(it) }
        return sortBuilder
    }

    private fun executeQuery(sortBuilder: SortBuilder, pageRequest: PageRequest): List<Any?> {
        val finalQuery = """ $ROOT_QUERY ${sortBuilder.buildSortStatement()}"""
        val query = entityManager.createNativeQuery(finalQuery)
        query.setFirstResult(pageRequest.pageNumber * pageRequest.pageSize)
        query.setMaxResults(pageRequest.pageSize)
        return query.resultList
    }

    private fun mapRawResponseToDataModel(rawResponse: List<Any?>) =
        rawResponse.map {
            val lst = it as Array<*>
            ProductDataModel(
                lst[0] as Int,
                lst[1] as String,
                lst[2] as Int,
                lst[3] as Int,
                lst[4] as Int,
                lst[5] as Int
            )
        }

    private fun calculateTotalElements() =
        entityManager.createNativeQuery(COUNT_QUERY).singleResult.toString().toLong()

}