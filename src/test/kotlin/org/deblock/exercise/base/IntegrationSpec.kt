package org.deblock.exercise.base

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class IntegrationSpec : Specification() {

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun registerPgProperties(registry: DynamicPropertyRegistry) {
            registry.add("wiremock.port") { WiremockSupport.wiremockPort }
        }
    }
}

class WiremockSupport {
    companion object {
        val wiremockPort = (1024..65536).random()
        val wiremock by lazy {
            WireMockServer(wiremockPort).apply { start() }
        }
    }
}