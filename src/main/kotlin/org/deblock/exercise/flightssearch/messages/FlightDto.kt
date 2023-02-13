package org.deblock.exercise.flightssearch.messages

import java.math.BigDecimal
import java.time.ZonedDateTime

public data class FlightDto(
    val airline: String,
    val supplier: String,
    val fare: BigDecimal,
    val departureAirportCode: String,
    val destinationAirportCode: String,
    val departureDate: ZonedDateTime,
    val arrivalDate: ZonedDateTime,
) {
    public companion object
}