package pl.mbatyra.externalservice

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class InvoiceEndpoint {

    private val logger by logger()

    @GetMapping("/demo2/invoices/{userId}")
    fun getUserInvoicesDemo2(@PathVariable userId: String): ResponseEntity<UserInvoicesDto> {
        logger.info("Rest request to get user $userId invoices")
        return INVOICES[userId]?.let {
            ResponseEntity.ok(UserInvoicesDto(it))
        } ?: ResponseEntity.notFound().build()
    }
}