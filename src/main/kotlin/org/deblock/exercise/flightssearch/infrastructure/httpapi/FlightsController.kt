package org.deblock.exercise.flightssearch.infrastructure.httpapi

import am.ik.yavi.builder.validator
import org.deblock.exercise.flightssearch.FlightsSearchModule
import org.deblock.exercise.flightssearch.messages.FlightDto
import org.deblock.exercise.flightssearch.messages.query.FlightsSearchQuery
import org.deblock.exercise.libs.throwIfInvalid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/flights")
private class FlightsController(
    private val flights: FlightsSearchModule,
) {
    @GetMapping("/search")
    suspend fun search(query: FlightSearchQueryParams): List<FlightDto> {
        return flights.search(FlightsSearchQuery(
            origin = query.origin!!,
            destination = query.destination!!,
            departureDate = LocalDate.parse(query.departureDate),
            returnDate = LocalDate.parse(query.departureDate),
            numberOfPassengers = query.numberOfPassengers!!,
        )).flights
    }

    class FlightSearchQueryParams(
        val origin: String?,
        val destination: String?,
        val departureDate: String?,
        val returnDate: String?,
        val numberOfPassengers: Int?
    ) {
        init {
            validator.throwIfInvalid(this)
        }
        companion object {
            private val validator = validator {
                FlightSearchQueryParams::origin {
                    notNull()
                    fixedSize(3)
                }
                FlightSearchQueryParams::destination {
                    notNull()
                    fixedSize(3)
                }
                FlightSearchQueryParams::departureDate {
                    notNull()
                    isIsoLocalDate
                }
                FlightSearchQueryParams::returnDate {
                    isIsoLocalDate
                }
                FlightSearchQueryParams::numberOfPassengers {
                    notNull()
                    greaterThanOrEqual(1)
                    lessThanOrEqual(4)
                }
            }
        }
    }

}
