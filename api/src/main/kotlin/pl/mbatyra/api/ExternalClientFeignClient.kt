package pl.mbatyra.api

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(value = "external-service", url = "http://localhost:8081")
interface ExternalServiceFeignClient {
    @GetMapping("/demo3/invoices/{userId}")
    fun getUserInvoicesDemo3(@PathVariable("userId") userId: String): UserInvoicesDto
}