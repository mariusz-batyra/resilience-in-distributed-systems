package pl.mbatyra.api

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/resilience4j"], produces = [MediaType.APPLICATION_JSON_VALUE])
class DemoResilienceEndpoint(
    private val resilienceExternalClient: ResilienceExternalClient
) {

    private val logger by logger()

    @GetMapping("/users/{id}")
    fun getUserDetails(@PathVariable id: String): ResponseEntity<UserDto> {
        logger.info("Rest request to get user by id: $id")
        return USERS.find { it.id == id }
            ?.let { UserDto(id = it.id, login = it.login, invoices = resilienceExternalClient.getUserInvoices(id)) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }
}