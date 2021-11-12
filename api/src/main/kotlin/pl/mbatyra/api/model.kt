package pl.mbatyra.api

data class UserDto(
    val id: String,
    val login: String,
    val invoices: List<UserInvoiceDto>
)

data class UserInvoicesDto(val invoices: List<UserInvoiceDto>)
data class UserInvoiceDto(val content: String)