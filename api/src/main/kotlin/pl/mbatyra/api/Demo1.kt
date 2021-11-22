package pl.mbatyra.api

import io.micrometer.core.annotation.Timed
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/demo1"], produces = [MediaType.APPLICATION_JSON_VALUE])
class Demo1Endpoint {

    private val logger by logger()

    @GetMapping("/users")
    fun getUsers(): ResponseEntity<List<UserDto>> {
        logger.info("Rest request to get all users")
        Thread.sleep(250)
        return ResponseEntity.ok(USERS)
    }

    @GetMapping("/users/{id}")
    fun getUserDetails(@PathVariable id: String): ResponseEntity<UserDto> {
        logger.info("Rest request to get user by id: $id")
        Thread.sleep(250)
        return USERS.find { it.id == id }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }
}