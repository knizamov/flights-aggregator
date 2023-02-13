package org.deblock.exercise.flightssearch.infrastructure.httpapi

import am.ik.yavi.core.ViolationDetail
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain
import org.deblock.exercise.base.*
import org.deblock.exercise.flightssearch.infrastructure.suppliers.crazyair.CrazyAirSamples.Companion.stubCrazyAirFlights
import org.deblock.exercise.flightssearch.infrastructure.suppliers.toughjet.ToughJetSamples.Companion.stubToughJetFlights
import org.deblock.exercise.flightssearch.messages.FlightDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList


class FlightsControllerIntegrationSpec(
    @Autowired private val webClient: WebTestClient,
) : IntegrationSpec() {

    @Test
    fun `aggregates result from multiple suppliers ordered by fare asc`() {
        Given("CrazyAir and ToughJet have flights")
        stubToughJetFlights()
        stubCrazyAirFlights()

        When("Searching flights")
        val result = webClient.get().uri {
            it.path("/flights/search")
                .queryParam("origin", "PRG")
                .queryParam("destination", "JFK")
                .queryParam("departureDate", "2021-06-01")
                .queryParam("returnDate", "2021-12-01")
                .queryParam("numberOfPassengers", "4")
                .build()
        }.exchange()

        Then("Results are aggregated from all suppliers ordered by fare ascending")
        result.expectStatus().is2xxSuccessful
        val flights = result.bodyList<FlightDto>()
        flights.shouldHaveSize(4)
        flights[0].fare eq 99.00.toBigDecimal()
        flights[1].fare eq 198.00.toBigDecimal()
        flights[2].fare eq 1000.25.toBigDecimal()
        flights[3].fare eq 2000.25.toBigDecimal()
    }

    @ParameterizedTest
    @ArgumentsSource(C1::class) // no need to use companion object or change test lifecycle
    fun `basic validation rules`(case: Case1) {
        val (queryParam, value, rule) = case
        Given("invalid $queryParam = $value")
        When("Searching with invalid params")
        val result = webClient.get().uri {
            it.path("/flights/search")
                .queryParam("origin", "PRG")
                .queryParam("destination", "JFK")
                .queryParam("departureDate", "2021-06-01")
                .queryParam("returnDate", "2021-06-15")
                .queryParam("numberOfPassengers", "4")
                .replaceQueryParam(queryParam, value)
                .build()
        }.exchange()

        Then("Error is returned due to $rule")
        result.expectStatus().isBadRequest

        val violations = result.bodyList<ViolationDetail>()
        violations.shouldHaveSize(1)

        val violation = violations.first()
        violation.defaultMessage shouldContain rule
    }

    data class Case1(val queryParam: String, val value: String?, val rule: String)
    class C1 : Where({
        of(Case1(queryParam = "origin", value = null, rule = """"origin" must not be null"""))
        of(Case1(queryParam = "origin", value = "", rule = """The size of "origin" must be 3"""))
        of(Case1(queryParam = "origin", value = "ABCD", rule = """The size of "origin" must be 3"""))
        of(Case1(queryParam = "destination", value = null, rule = """"destination" must not be null"""))
        of(Case1(queryParam = "destination", value = "", rule = """The size of "destination" must be 3"""))
        of(Case1(queryParam = "destination", value = "ABCD", rule = """The size of "destination" must be 3"""))
        of(Case1(queryParam = "departureDate", value = null, rule = """"departureDate" must not be null"""))
        of(Case1(queryParam = "departureDate", value = "2021", rule = """"departureDate" must be a valid representation of a local date using the pattern: uuuu-MM-dd."""))
        of(Case1(queryParam = "returnDate", value = "2021", rule = """"returnDate" must be a valid representation of a local date using the pattern: uuuu-MM-dd"""))
        of(Case1(queryParam = "numberOfPassengers", value = "0", rule = """"numberOfPassengers" must be greater than or equal to 1"""))
        of(Case1(queryParam = "numberOfPassengers", value = "5", rule = """"numberOfPassengers" must be less than or equal to 4"""))
    })
}

private inline fun <reified T : Any> WebTestClient.ResponseSpec.bodyList(): List<T> {
    return expectBodyList<T>().returnResult().responseBody!!
}
