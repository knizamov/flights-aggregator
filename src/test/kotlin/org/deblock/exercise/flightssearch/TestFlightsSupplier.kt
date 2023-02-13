package org.deblock.exercise.flightssearch

import kotlinx.coroutines.delay
import org.deblock.exercise.flightssearch.messages.FlightDto
import java.time.Duration


internal class TestFlightsSupplier : FlightsSupplier {
    private var flights = mutableListOf<FlightDto>()

    private var error: RuntimeException? = null
    private var delay: Duration? = null

    override suspend fun searchFlights(query: FlightsSearchQuery): List<FlightDto> {
        if (delay != null) delay(delay!!.toMillis())
        if (error != null) throw error!!
        return flights
    }

    fun has(flight: FlightDto): FlightDto {
        this.flights.add(flight)
        return flight
    }

    fun willReturnError() {
        this.error = RuntimeException()
    }

    fun isSlow(delay: Duration) {
        this.delay = delay
    }
}