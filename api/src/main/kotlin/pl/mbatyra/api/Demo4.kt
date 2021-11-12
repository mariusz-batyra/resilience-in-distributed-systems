package pl.mbatyra.api

import feign.Retryer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/demo4"], produces = [MediaType.APPLICATION_JSON_VALUE])
class Demo4Endpoint(
    private val externalServiceFeignClient: ExternalServiceFeignClient
) {

    private val logger by logger()

    @GetMapping("/users/{id}")
    fun getUserDetails(@PathVariable id: String): ResponseEntity<UserDto> {
        logger.info("Rest request to get user by id: $id")
        return USERS.find { it.id == id }
            ?.let { UserDto(id = it.id, login = it.login, invoices = externalServiceFeignClient.getUserInvoicesDemo4(id).invoices) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }
}

@Configuration
class FeignClientConfig {
    @Bean
    fun retryer(): Retryer {
        return Retryer.Default(100, 2000, 3)
    }
}