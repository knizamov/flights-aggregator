package org.deblock.exercise.flightssearch

import org.deblock.exercise.flightssearch.messages.FlightDto
import org.deblock.exercise.flightssearch.messages.query.FlightsSearchQuery
import java.time.ZonedDateTime

fun FlightDto.Companion.any(): FlightDto {
    val departureDate = randomDepartureDate()
    return FlightDto(
        airline = randomAirline(),
        supplier = randomSupplier(),
        fare = randomFare(),
        departureAirportCode = "PRG",
        destinationAirportCode = "JFK",
        departureDate = departureDate,
        arrivalDate = departureDate.plusMinutes((15..720L).random())
    )
}

fun FlightsSearchQuery.Companion.covering(flight: FlightDto): FlightsSearchQuery {
    return FlightsSearchQuery(
        origin = flight.departureAirportCode,
        destination = flight.destinationAirportCode,
        departureDate = flight.departureDate.toLocalDate(),
        returnDate = null,
        numberOfPassengers = (1..4).random()
    )
}

private fun randomNumber() = (0..99999L).random()
private fun randomAirline() = "Airline" + randomNumber()
private fun randomSupplier() = "Supplier" + ('A'..'Z').random()
private fun randomFare() = randomNumber().toBigDecimal().setScale(2)
private fun randomDepartureDate(): ZonedDateTime {
    val yearInHours = 8760L
    val upToYear = (0..yearInHours).random()
    return ZonedDateTime.now().plusHours(upToYear)
}
