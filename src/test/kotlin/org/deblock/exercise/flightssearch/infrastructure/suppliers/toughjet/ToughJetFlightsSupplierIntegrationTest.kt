package org.deblock.exercise.flightssearch.infrastructure.suppliers.toughjet

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.date.shouldHaveSameInstantAs
import kotlinx.coroutines.test.runTest
import org.deblock.exercise.base.*
import org.deblock.exercise.base.WiremockSupport.Companion.wiremock
import org.deblock.exercise.flightssearch.FlightsSearchQuery
import org.deblock.exercise.flightssearch.infrastructure.suppliers.toughjet.ToughJetSamples.Companion.stubToughJetFlights
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset.UTC

internal class ToughJetFlightsSupplierIntegrationTest(
    @Autowired private val crazyAirSupplier: ToughJetFlightsSupplier,
    @Autowired private val objectMapper: ObjectMapper,
) : IntegrationSpec() {

    @BeforeEach
    fun setup() {
        wiremock.resetAll()
    }

    @Test
    fun `Integration works, supplier sends proper request and maps result correctly`() = runTest {
        Given("Supplier has flights")
        stubToughJetFlights()

        When("Calling Supplier")
        val query = FlightsSearchQuery(
            origin = "PRG",
            destination = "JFK",
            departureDate = LocalDate.parse("2021-06-15"),
            returnDate = LocalDate.parse("2021-06-21"),
            numberOfPassengers = 4
        )
        val flights = crazyAirSupplier.searchFlights(query)

        Then("Supplier sent properly mapped request")
        val sentRequest = ToughJetFlightsSupplier.ToughJetRequest(
            from = query.origin,
            to = query.destination,
            outboundDate = query.departureDate,
            inboundDate = query.returnDate,
            numberOfAdults = query.numberOfPassengers
        )
        wiremock.verify(postRequestedFor(urlEqualTo("/toughjet/flights"))
            .withRequestBody(equalToJson(objectMapper.writeValueAsString(sentRequest))))

        And("Supplier properly mapped response")
        flights.shouldHaveSize(2)
        flights[0].airline eq "CarrierA"
        flights[0].supplier eq "ToughJet"
        flights[0].departureAirportCode eq "PRG"
        flights[0].destinationAirportCode eq "JFK"
        flights[0].departureDate shouldHaveSameInstantAs Instant.parse("2021-06-21T08:00:00.000Z").atZone(UTC)
        flights[0].arrivalDate shouldHaveSameInstantAs Instant.parse("2021-06-22T12:00:00.000Z").atZone(UTC)
        flights[0].fare notEq null

        flights[1].airline eq "CarrierB"
        flights[1].supplier eq "ToughJet"
        flights[1].departureAirportCode eq "PRG"
        flights[1].destinationAirportCode eq "JFK"
        flights[1].departureDate shouldHaveSameInstantAs Instant.parse("2021-06-21T08:00:00.000Z").atZone(UTC)
        flights[1].arrivalDate shouldHaveSameInstantAs Instant.parse("2021-06-22T12:00:00.000Z").atZone(UTC)
        flights[1].fare notEq null
    }


    @Test
    fun `Calculates fare with tax and discount applied pre tax`() = runTest {
        Given()
        stubToughJetFlights()

        When()
        val query = FlightsSearchQuery(
            origin = "PRG",
            destination = "JFK",
            departureDate = LocalDate.parse("2021-06-15"),
            returnDate = LocalDate.parse("2021-06-21"),
            numberOfPassengers = 4
        )
        val result = crazyAirSupplier.searchFlights(query)

        Then("Fare calculated with discount applied pre tax")
        val flight = result.first()
        val basePrice = 100.00.toBigDecimal()
        val discountPercentage = 0.10.toBigDecimal()
        val taxPercentage = 0.10.toBigDecimal()

        val discountAmountPreTax = basePrice * discountPercentage // 10$ discount
        val discountedPriceWithoutTax = basePrice - discountAmountPreTax // 100$ - 10$ = 90$
        val taxAmount = discountedPriceWithoutTax * taxPercentage // 90 * 0.10 = 9$

        flight.fare shouldBeEqualComparingTo discountedPriceWithoutTax + taxAmount
    }
}