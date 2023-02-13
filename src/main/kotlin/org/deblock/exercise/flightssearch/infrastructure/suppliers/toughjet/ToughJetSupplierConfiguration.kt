package org.deblock.exercise.flightssearch.infrastructure.suppliers.toughjet

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(ToughJetConfigurationProperties::class)
private class ToughJetSupplierConfiguration {

    @Bean
    fun toughJetSupplier(
        webClientBuilder: WebClient.Builder,
        properties: ToughJetConfigurationProperties
    ): ToughJetFlightsSupplier {
        return ToughJetFlightsSupplier(webClientBuilder, properties)
    }
}