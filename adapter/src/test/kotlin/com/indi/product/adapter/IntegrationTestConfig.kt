package com.indi.product.adapter

import com.indi.product.adapter.out.persistence.jpa.SearchProductsJPARepository
import com.indi.product.application.port.`in`.SearchProductsUseCase
import com.indi.product.application.port.out.SearchProductsRepository
import com.indi.product.application.service.SearchProductsService
import jakarta.persistence.EntityManager
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan("com.indi")
class IntegrationTestConfig {

    @Bean
    fun searchProductsJPARepository(entityManager: EntityManager): SearchProductsJPARepository {
        return SearchProductsJPARepository(entityManager)
    }

    @Bean
    fun searchProductsUseCase(searchProductsRepository: SearchProductsRepository): SearchProductsUseCase {
        return SearchProductsService(searchProductsRepository)
    }
}