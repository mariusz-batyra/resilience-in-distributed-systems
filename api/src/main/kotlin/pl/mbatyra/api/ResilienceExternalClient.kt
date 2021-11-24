package pl.mbatyra.api

import org.springframework.stereotype.Component

@Component
class ResilienceExternalClient {
    fun getUserInvoices(userId: String): List<UserInvoiceDto> {
        TODO("implement me, please!")
    }
}