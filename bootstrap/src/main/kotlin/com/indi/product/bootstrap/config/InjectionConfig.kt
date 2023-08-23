package com.indi.product.bootstrap.config

import com.indi.product.adapter.out.persistence.jpa.SearchProductsJPARepository
import com.indi.product.application.port.`in`.SearchProductsUseCase
import com.indi.product.application.port.out.SearchProductsRepository
import com.indi.product.application.service.SearchProductsService
import jakarta.persistence.EntityManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class InjectionConfig {

    @Bean
    fun searchProductsRepository(entityManager: EntityManager): SearchProductsRepository {
        return SearchProductsJPARepository(entityManager)
    }

    @Bean
    fun searchProductsUseCase(searchProductsRepository: SearchProductsRepository): SearchProductsUseCase {
        return SearchProductsService(searchProductsRepository)
    }
}