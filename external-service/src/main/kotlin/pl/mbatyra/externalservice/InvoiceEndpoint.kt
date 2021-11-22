package pl.mbatyra.externalservice

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Random

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class InvoiceEndpoint {

    private val logger by logger()

    @GetMapping("/demo2/invoices/{userId}")
    fun getUserInvoicesDemo2(@PathVariable userId: String): ResponseEntity<UserInvoicesDto> {
        logger.info("Rest request to get user $userId invoices")
        Thread.sleep(10 * 1000L) // 10 sec
        return INVOICES[userId]?.let {
            ResponseEntity.ok(UserInvoicesDto(it))
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/demo3/invoices/{userId}")
    fun getUserInvoicesDemo3(@PathVariable userId: String): ResponseEntity<UserInvoicesDto> {
        logger.info("Rest request to get user $userId invoices")
        return INVOICES[userId]?.let {
            ResponseEntity.ok(UserInvoicesDto(it))
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/demo4/invoices/{userId}")
    fun getUserInvoicesDemo4(@PathVariable userId: String): ResponseEntity<UserInvoicesDto> {
        logger.info("Rest request to get user $userId invoices")
        val timeout = Random().nextInt(600)
        logger.info("applied timeout: $timeout ms")
        Thread.sleep(timeout.toLong())
        return INVOICES[userId]?.let {
            ResponseEntity.ok(UserInvoicesDto(it))
        } ?: ResponseEntity.notFound().build()
    }
}