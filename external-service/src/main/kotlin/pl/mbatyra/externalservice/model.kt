package pl.mbatyra.externalservice

data class UserInvoicesDto(val invoices: List<UserInvoiceDto>)
data class UserInvoiceDto(val content: String)