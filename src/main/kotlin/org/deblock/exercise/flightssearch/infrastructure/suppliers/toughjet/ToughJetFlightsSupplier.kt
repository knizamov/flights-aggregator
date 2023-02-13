package org.deblock.exercise.flightssearch.infrastructure.suppliers.toughjet

import org.deblock.exercise.flightssearch.FlightsSearchQuery
import org.deblock.exercise.flightssearch.FlightsSupplier
import org.deblock.exercise.flightssearch.messages.FlightDto
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

internal class ToughJetFlightsSupplier(
    webClientBuilder: WebClient.Builder,
    private val properties: ToughJetConfigurationProperties
) : FlightsSupplier {
    private val webClient = webClientBuilder.baseUrl(properties.baseUrl).build()

    override suspend fun searchFlights(query: FlightsSearchQuery): List<FlightDto> {
        val toughJetFlights = findToughJetFlights(ToughJetRequest(
            from = query.origin,
            to = query.destination,
            outboundDate = query.departureDate,
            inboundDate = query.returnDate,
            numberOfAdults = query.numberOfPassengers
        ))

        return toughJetFlights.map {
            val arrivalAirportTimezone = resolveTimezone(it.departureAirportName)
            val destinationTimezone = resolveTimezone(it.arrivalAirportName)
            FlightDto(
                airline = it.carrier,
                supplier = "ToughJet",
                fare = calculateFare(it),
                departureAirportCode = it.departureAirportName,
                destinationAirportCode = it.arrivalAirportName,
                departureDate = it.outboundDateTime.atZone(arrivalAirportTimezone),
                arrivalDate = it.inboundDateTime.atZone(destinationTimezone)
            )
        }
    }

    private suspend fun findToughJetFlights(request: ToughJetRequest): List<ToughJetFlight> {
        return webClient.post()
            .uri("/flights")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .awaitBody()
    }

    internal data class ToughJetRequest(
        val from: String,
        val to: String,
        val outboundDate: LocalDate,
        val inboundDate: LocalDate?,
        val numberOfAdults: Int,
    )

    internal data class ToughJetFlight(
        val carrier: String,
        val basePrice: BigDecimal,
        val tax: BigDecimal, // percentage, represented as decimal 0.00-1.00
        val discount: BigDecimal, // let's assume 0 is returned if no discount
        val departureAirportName: String,
        val arrivalAirportName: String,
        val outboundDateTime: Instant,
        val inboundDateTime: Instant,
    )

    private fun resolveTimezone(airportName: String): ZoneId {
        return ZoneId.of("UTC") // simplification as discussed
    }

    private fun calculateFare(flight: ToughJetFlight): BigDecimal {
        val basePriceWithoutTax = flight.basePrice
        val discountPercentage = flight.discount
        val taxPercentage = flight.tax

        val discountAmount = basePriceWithoutTax * discountPercentage // Discount is applied pre-tax
        val discountedPrice = basePriceWithoutTax - discountAmount

        val taxAmount = discountedPrice * taxPercentage
        return discountedPrice + taxAmount
    }

}
