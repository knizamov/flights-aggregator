package org.deblock.exercise.flightssearch

import org.deblock.exercise.flightssearch.messages.FlightDto
import java.time.LocalDate

public fun interface FlightsSupplier {
    public suspend fun searchFlights(query: FlightsSearchQuery): List<FlightDto>
}

public data class FlightsSearchQuery(
    val origin: String,
    val destination: String,
    val departureDate: LocalDate,
    val returnDate: LocalDate?,
    val numberOfPassengers: Int,
)
