package org.deblock.exercise.flightssearch.messages.query

import org.deblock.exercise.flightssearch.messages.FlightDto
import java.time.LocalDate

public data class FlightsSearchQuery(
    val origin: String,
    val destination: String,
    val departureDate: LocalDate,
    val returnDate: LocalDate?,
    val numberOfPassengers: Int,
) {
    public data class Result(val flights: List<FlightDto>)
    public companion object
}