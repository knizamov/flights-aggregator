package org.deblock.exercise.flightssearch.infrastructure.suppliers.crazyair

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("supplier.crazyair")
@ConstructorBinding
internal data class CrazyAirConfigurationProperties(val baseUrl: String)