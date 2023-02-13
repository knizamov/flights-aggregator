package org.deblock.exercise.flightssearch

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.test.runTest
import org.deblock.exercise.base.*
import org.deblock.exercise.flightssearch.messages.FlightDto
import org.deblock.exercise.flightssearch.messages.query.FlightsSearchQuery
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime


class FlightsSearchModuleSpec : Specification() {

    private val supplierA = TestFlightsSupplier()
    private val supplierB = TestFlightsSupplier()

    private val flights = FlightsSearchModuleConfiguration().flightsSearchModule(
        suppliers = listOf(supplierA, supplierB)
    )

    private val today = LocalDate.now()
    private val now = ZonedDateTime.now()

    @Test
    fun `aggregates flights from multiple suppliers ordered by fare ascending`() = runTest {
        Given("Supplier A has 1 the cheapest flight")
        val cheapestFlight = supplierA.has(FlightDto(
            airline = "AirlineA",
            supplier = "A",
            fare = "1.00".toBigDecimal(),
            departureAirportCode = "PRG",
            destinationAirportCode = "JFK",
            departureDate = now,
            arrivalDate = now.plusHours(6)
        ))

        And("Supplier B has 1 more most expensive flight and 1 mid price flight")
        val mostExpensiveFlight = supplierB.has(FlightDto(
            airline = "AirlineB",
            supplier = "B",
            fare = "100.00".toBigDecimal(),
            departureAirportCode = "PRG",
            destinationAirportCode = "JFK",
            departureDate = now,
            arrivalDate = now.plusHours(1)
        ))

        val midPriceFlight = supplierB.has(FlightDto(
            airline = "AirlineC",
            supplier = "B",
            fare = "50.00".toBigDecimal(),
            departureAirportCode = "PRG",
            destinationAirportCode = "JFK",
            departureDate = now,
            arrivalDate = now.plusHours(1)
        ))

        When("Searching for flights")
        val query = FlightsSearchQuery(
            origin = "PRG",
            destination = "NYC",
            departureDate = today,
            returnDate = today.plusDays(7),
            numberOfPassengers = 1,
        )
        val result = flights.search(query)

        Then("Aggregated result from all suppliers are returned ordered by fare ASC")
        result.flights.shouldHaveSize(3)
        result.flights[0] eq cheapestFlight
        result.flights[1] eq midPriceFlight
        result.flights[2] eq mostExpensiveFlight
    }

    @Test
    fun `empty result is returned if none of the suppliers returned anything`() = runTest {
        Expect("Empty result")
        val flights = flights.search(FlightsSearchQuery(
            origin = "PRG",
            destination = "NYC",
            departureDate = today,
            returnDate = today.plusDays(7),
            numberOfPassengers = 1,
        )).flights
        flights.shouldBeEmpty()
    }

    @Test
    fun `fare is rounded up to 2 decimal places`() = runTest {
        Given("Supplier returns fare with more then 2 decimal places")
        val longFare = "111.155555"
        val flight = FlightDto.any().copy(fare = longFare.toBigDecimal())
        supplierA.has(flight)

        When()
        val flights = flights.search(FlightsSearchQuery.covering(flight)).flights

        Then()
        flights.first().fare eq "111.16".toBigDecimal()
    }

    @Test
    fun `if one of the suppliers returns error, then it's skipped`() = runTest {
        Given("One of the suppliers throws error")
        val flight = supplierA.has(FlightDto.any())
        supplierB.willReturnError()

        When("Searching for flights")
        val result = flights.search(FlightsSearchQuery.covering(flight))

        Then("Erroneous supplier is skipped")
        result.flights.shouldContainOnly(flight)
    }

    @Test
    fun `is one of the supplier is being slow, then it's skipped`() = runTest {
        Given("One of the suppliers is slow")
        val flight = supplierA.has(FlightDto.any())

        val slowFlight = supplierB.has(FlightDto.any())
        supplierB.isSlow(Duration.ofSeconds(1))

        When("Searching for flights")
        val result = flights.search(FlightsSearchQuery.covering(flight))

        Then("Erroneous supplier is skipped")
        result.flights.shouldContainOnly(flight)
    }
}

