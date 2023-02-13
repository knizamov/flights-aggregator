package org.deblock.exercise.flightssearch

import org.deblock.exercise.flightssearch.messages.FlightDto
import org.deblock.exercise.flightssearch.messages.query.FlightsSearchQuery
import java.math.RoundingMode.HALF_UP


public class FlightsSearchModule internal constructor(
    private val flightsSupplier: FlightsSupplier,
) {

    public suspend fun search(query: FlightsSearchQuery): FlightsSearchQuery.Result {
        val foundFlights = flightsSupplier.searchFlights(org.deblock.exercise.flightssearch.FlightsSearchQuery(
            origin = query.origin,
            destination = query.destination,
            departureDate = query.departureDate,
            returnDate = query.returnDate,
            numberOfPassengers = query.numberOfPassengers)
        )
        val sortedFlights = foundFlights
            .sortedBy { it.fare }
            .map { roundUpFare(it) }
        return FlightsSearchQuery.Result(sortedFlights)
    }

    private fun roundUpFare(flight: FlightDto) = flight.copy(
        fare = flight.fare.setScale(2, HALF_UP)
    )
}
