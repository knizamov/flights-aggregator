package org.deblock.exercise.flightssearch.infrastructure.suppliers.crazyair

import org.deblock.exercise.flightssearch.FlightsSearchQuery
import org.deblock.exercise.flightssearch.FlightsSupplier
import org.deblock.exercise.flightssearch.messages.FlightDto
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

internal class CrazyAirSupplier(
    webClientBuilder: WebClient.Builder,
    properties: CrazyAirConfigurationProperties
) : FlightsSupplier {
    private val webClient = webClientBuilder.baseUrl(properties.baseUrl).build()

    override suspend fun searchFlights(query: FlightsSearchQuery): List<FlightDto> {
        val flights = findCrazyAirFlights(CrazyAirRequest(
            origin = query.origin,
            destination = query.destination,
            departureDate = query.departureDate,
            returnDate = query.returnDate,
            passengerCount = query.numberOfPassengers
        ))

        return flights.map {
            val departureAirportTimezone = resolveTimezone(it.departureAirportCode)
            val destinationAirportTimezone = resolveTimezone(it.destinationAirportCode)
            FlightDto(
                airline = it.airline,
                supplier = "CrazyAir",
                fare = it.price,
                departureAirportCode = it.departureAirportCode,
                destinationAirportCode = it.destinationAirportCode,
                departureDate = it.departureDate.atZone(departureAirportTimezone),
                arrivalDate = it.arrivalDate.atZone(destinationAirportTimezone)
            )
        }
    }

    private suspend fun findCrazyAirFlights(request: CrazyAirRequest): List<CrazyAirFlight> {
        return webClient.post()
            .uri("/flights")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .awaitBody()
    }

    internal data class CrazyAirRequest(
        val origin: String,
        val destination: String,
        val departureDate: LocalDate,
        val returnDate: LocalDate?,
        val passengerCount: Int
    )

    internal data class CrazyAirFlight(
        val airline: String,
        val price: BigDecimal,
        val cabinclass: String,
        val departureAirportCode: String,
        val destinationAirportCode: String,
        val departureDate: LocalDateTime,
        val arrivalDate: LocalDateTime,
    )

    private fun resolveTimezone(airportName: String): ZoneId {
        return ZoneId.of("UTC") // simplification as discussed
    }
}