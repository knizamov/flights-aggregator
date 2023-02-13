package org.deblock.exercise.flightssearch.infrastructure.suppliers.crazyair

import com.github.tomakehurst.wiremock.client.WireMock
import org.deblock.exercise.base.WiremockSupport.Companion.wiremock
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

class CrazyAirSamples {
    companion object {
        fun stubCrazyAirFlights() {
            wiremock.stubFor(WireMock.post("/crazyair/flights").willReturn(WireMock.aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(
                    """
                        [
                          {
                            "airline": "AirlineA",
                            "price": 2000.25,
                            "cabinclass": "E",
                            "departureAirportCode": "PRG",
                            "destinationAirportCode": "JFK",
                            "departureDate": "2021-06-21T08:00:00.000Z",
                            "arrivalDate": "2021-06-21T18:00:00.000Z"
                          },
                          {
                            "airline": "AirlineB",
                            "price": 1000.25,
                            "cabinclass": "B",
                            "departureAirportCode": "PRG",
                            "destinationAirportCode": "JFK",
                            "departureDate": "2021-06-21T08:00:00.000Z",
                            "arrivalDate": "2021-06-21T18:00:00.000Z"
                          }
                        ]
                """.trimIndent()
                )
            ))
        }

    }
}