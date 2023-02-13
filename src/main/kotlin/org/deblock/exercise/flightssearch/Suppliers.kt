package org.deblock.exercise.flightssearch

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout
import org.deblock.exercise.flightssearch.messages.FlightDto

internal class AggregatingFlightsSupplier(
    private val suppliers: List<FlightsSupplier>
) : FlightsSupplier {
    override suspend fun searchFlights(query: FlightsSearchQuery): List<FlightDto> {
        return findFlightsInParallel(query)
    }

    private suspend fun findFlightsInParallel(query: FlightsSearchQuery): List<FlightDto> = coroutineScope {
        suppliers.map { supplier ->
            async { supplier.searchFlights(query) }
        }.awaitAll().flatten()
    }
}

internal class ResilientFlightSupplier(private val supplier: FlightsSupplier) : FlightsSupplier {
    override suspend fun searchFlights(query: FlightsSearchQuery): List<FlightDto> {
        return try {
            withTimeout(500) {
                supplier.searchFlights(query)
            }
        } catch (ex: Exception) {
            return emptyList()
        }
    }
}