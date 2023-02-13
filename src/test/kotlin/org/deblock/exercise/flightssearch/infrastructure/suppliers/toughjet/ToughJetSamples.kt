package org.deblock.exercise.flightssearch.infrastructure.suppliers.toughjet

import com.github.tomakehurst.wiremock.client.WireMock
import org.deblock.exercise.base.WiremockSupport.Companion.wiremock
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

class ToughJetSamples {
    companion object {
        fun stubToughJetFlights() {
            wiremock.stubFor(WireMock.post("/toughjet/flights").willReturn(
                WireMock.aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(
                        """
                            [
                              {
                                "carrier": "CarrierA",
                                "basePrice": 100.00,
                                "tax": 0.10,
                                "discount": 0.10,
                                "departureAirportName": "PRG",
                                "arrivalAirportName": "JFK",
                                "outboundDateTime": "2021-06-21T08:00:00.000Z",
                                "inboundDateTime": "2021-06-22T12:00:00.000Z"
                              },
                              {
                                "carrier": "CarrierB",
                                "basePrice": 200.00,
                                "tax": 0.10,
                                "discount": 0.10,
                                "departureAirportName": "PRG",
                                "arrivalAirportName": "JFK",
                                "outboundDateTime": "2021-06-21T08:00:00.000Z",
                                "inboundDateTime": "2021-06-22T12:00:00.000Z"
                              }
                            ]
                        """.trimIndent()
                    )
            ))
        }

    }
}