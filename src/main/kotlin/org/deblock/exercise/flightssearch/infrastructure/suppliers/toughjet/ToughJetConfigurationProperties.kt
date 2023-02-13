package org.deblock.exercise.flightssearch.infrastructure.suppliers.toughjet

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("supplier.toughjet")
@ConstructorBinding
internal data class ToughJetConfigurationProperties(val baseUrl: String)