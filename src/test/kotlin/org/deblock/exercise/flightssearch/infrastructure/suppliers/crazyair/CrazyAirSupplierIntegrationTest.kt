package org.deblock.exercise.flightssearch.infrastructure.suppliers.crazyair

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.date.shouldHaveSameInstantAs
import kotlinx.coroutines.test.runTest
import org.deblock.exercise.base.*
import org.deblock.exercise.base.WiremockSupport.Companion.wiremock
import org.deblock.exercise.flightssearch.FlightsSearchQuery
import org.deblock.exercise.flightssearch.infrastructure.suppliers.crazyair.CrazyAirSamples.Companion.stubCrazyAirFlights
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset.UTC

internal class CrazyAirSupplierIntegrationTest(
    @Autowired private val crazyAirSupplier: CrazyAirSupplier,
    @Autowired private val objectMapper: ObjectMapper,
) : IntegrationSpec() {

    @BeforeEach
    fun setup() {
        wiremock.resetAll()
    }

    @Test
    fun `Integration works, supplier sends proper request and maps result correctly`() = runTest {
        Given("Supplier has flights")
        stubCrazyAirFlights()

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
        val sentRequest = CrazyAirSupplier.CrazyAirRequest(
            origin = query.origin,
            destination = query.destination,
            departureDate = query.departureDate,
            returnDate = query.returnDate,
            passengerCount = query.numberOfPassengers
        )
        wiremock.verify(postRequestedFor(urlEqualTo("/crazyair/flights"))
            .withRequestBody(equalToJson(objectMapper.writeValueAsString(sentRequest))))

        And("Supplier properly mapped response")
        flights.shouldHaveSize(2)
        flights[0].airline eq "AirlineA"
        flights[0].supplier eq "CrazyAir"
        flights[0].departureAirportCode eq "PRG"
        flights[0].destinationAirportCode eq "JFK"
        flights[0].departureDate shouldHaveSameInstantAs Instant.parse("2021-06-21T08:00:00.000Z").atZone(UTC)
        flights[0].arrivalDate shouldHaveSameInstantAs Instant.parse("2021-06-21T18:00:00.000Z").atZone(UTC)
        flights[0].fare eq 2000.25.toBigDecimal()

        flights[1].airline eq "AirlineB"
        flights[1].supplier eq "CrazyAir"
        flights[1].departureAirportCode eq "PRG"
        flights[1].destinationAirportCode eq "JFK"
        flights[1].departureDate shouldHaveSameInstantAs Instant.parse("2021-06-21T08:00:00.000Z").atZone(UTC)
        flights[1].arrivalDate shouldHaveSameInstantAs Instant.parse("2021-06-21T18:00:00.000Z").atZone(UTC)
        flights[1].fare eq 1000.25.toBigDecimal()
    }
}