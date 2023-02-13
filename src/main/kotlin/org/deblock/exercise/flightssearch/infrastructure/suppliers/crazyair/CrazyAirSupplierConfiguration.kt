package org.deblock.exercise.flightssearch.infrastructure.suppliers.crazyair

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(CrazyAirConfigurationProperties::class)
private class CrazyAirSupplierConfiguration {

    @Bean
    fun crazyAirSupplier(
        webCliBuilder: WebClient.Builder,
        properties: CrazyAirConfigurationProperties
    ): CrazyAirSupplier {
        return CrazyAirSupplier(webCliBuilder, properties)
    }
}