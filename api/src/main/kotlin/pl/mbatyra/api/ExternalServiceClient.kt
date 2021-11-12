package pl.mbatyra.api

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.net.URI

@Component
class ExternalServiceClient(
    private val restTemplate: RestTemplate
) {

    private val logger by logger()

    fun getUserInvoices(userId: String): List<UserInvoiceDto> {
        logger.info("Call external service for user: $userId")
        val responseEntity = restTemplate.getForEntity(URI.create("http://localhost:8081/demo2/invoices/$userId"), UserInvoicesDto::class.java)
        logger.info("Response from external service for user: $userId: $responseEntity")
        return responseEntity.body?.invoices ?: listOf()
    }
}

@Configuration
class AppConfiguration {
    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder): RestTemplate {
        return restTemplateBuilder.build()
    }
}