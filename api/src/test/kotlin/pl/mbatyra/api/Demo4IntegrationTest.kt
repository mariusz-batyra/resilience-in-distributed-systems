package pl.mbatyra.api

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.http.Fault
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.context.junit.jupiter.SpringExtension


@SpringBootTest(classes = [ApiApplication::class],
    properties = ["application.environment=test"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Demo4IntegrationTest(
    @Autowired private val restTemplate: TestRestTemplate
) {

    private val REQUEST_PREFIX = "/demo4/users/"

    val wireMockServer = WireMockServer(8081)

    @BeforeAll
    fun beforeAll() {
        wireMockServer.start()
    }

    @AfterEach
    fun afterEach() {
        wireMockServer.resetAll()
    }

    @Test
    fun shouldReturn404WhenUserDoesNotExits() {
        // given
        val userId = "unknown-user"

        // when
        val responseEntity = restTemplate.getForEntity(REQUEST_PREFIX + userId, Any::class.java)

        // then
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun shouldReturn500WhenExternalServiceIsDown() {
        // given
        val userId = "1"

        // when
        val responseEntity = restTemplate.getForEntity(REQUEST_PREFIX + userId, Any::class.java)

        // then
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun shouldReturn200() {
        // given
        val userId = "1"
        wireMockServer.stubFor(get("/demo4/invoices/$userId")
            .willReturn(aResponse()
                .withBody(""" { "invoices": [{"content": "invoice-1"}, {"content": "invoice-2"}, {"content": "invoice-3"}] }""".trimIndent())
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withStatus(200)
            ))

        // when
        val responseEntity = restTemplate.getForEntity(REQUEST_PREFIX + userId, UserDto::class.java)

        // then
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseEntity.body?.login).isEqualTo("admin")
        assertThat(responseEntity.body?.invoices).hasSize(3)
    }

    @Test
    fun shouldReturn500WhenConnectionWasResetByPeer() {
        // given
        val userId = "1"
        wireMockServer.stubFor(get("/demo4/invoices/$userId")
            .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)))

        // when
        val responseEntity = restTemplate.getForEntity(REQUEST_PREFIX + userId, Any::class.java)

        // then
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun shouldReturn500WhenExternalClientTimeoutOccurred() {
        // given
        val userId = "1"
        wireMockServer.stubFor(get("/demo4/invoices/$userId")
            .willReturn(aResponse()
                .withBody(""" { "invoices": [{"content": "invoice-1"}, {"content": "invoice-2"}, {"content": "invoice-3"}] }""".trimIndent())
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withStatus(200)
                .withFixedDelay(1000)
            ))

        // when
        val responseEntity = restTemplate.getForEntity(REQUEST_PREFIX + userId, Any::class.java)

        // then
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
