package pl.mbatyra.api

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/demo2"], produces = [MediaType.APPLICATION_JSON_VALUE])
class Demo2Endpoint(
    private val externalServiceClient: ExternalServiceClient
) {

    private val logger by logger()

    @GetMapping("/users")
    fun getUserDetails(): ResponseEntity<List<UserDto>> {
        logger.info("Rest request to get all users")
        val userWithInvoices = USERS.map {
            UserDto(id = it.id, login = it.login, invoices = externalServiceClient.getUserInvoices(it.id))
        }
        return ResponseEntity.ok(userWithInvoices)
    }

    @GetMapping("/users/{id}")
    fun getUserDetails(@PathVariable id: String): ResponseEntity<UserDto> {
        logger.info("Rest request to get user by id: $id")
        return USERS.find { it.id == id }
            ?.let { UserDto(id = it.id, login = it.login, invoices = externalServiceClient.getUserInvoices(it.id)) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }
}