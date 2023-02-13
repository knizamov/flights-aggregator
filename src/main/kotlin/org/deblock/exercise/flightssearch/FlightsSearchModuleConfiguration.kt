package org.deblock.exercise.flightssearch

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class FlightsSearchModuleConfiguration {

    @Bean
    fun flightsSearchModule(
        suppliers: List<FlightsSupplier>
    ): FlightsSearchModule {
        val resilientSuppliers = suppliers.map { ResilientFlightSupplier(it) }
        return FlightsSearchModule(
            AggregatingFlightsSupplier(resilientSuppliers)
        )
    }
}