package pl.mbatyra.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandler
import java.net.http.HttpResponse.BodySubscriber
import java.net.http.HttpResponse.ResponseInfo
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture


@RestController
@RequestMapping(value = ["/demo3"], produces = [MediaType.APPLICATION_JSON_VALUE])
class Demo3Endpoint(
    private val externalServiceWithDifferentHttpClient: ExternalServiceWithDifferentHttpClient
) {

    private val logger by logger()

    @GetMapping("/users/{id}/{client}")
    fun getUserDetails(@PathVariable id: String, @PathVariable client: String): ResponseEntity<UserDto> {
        logger.info("Rest request to get user by id: $id")
        return USERS.find { it.id == id }
            ?.let { UserDto(id = it.id, login = it.login, invoices = externalServiceWithDifferentHttpClient.getUserInvoices(it.id, client)) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }
}

@Component
class ExternalServiceWithDifferentHttpClient(
    private val externalServiceFeignClient: ExternalServiceFeignClient
) {

    private val logger by logger()

    fun getUserInvoices(userId: String, client: String): List<UserInvoiceDto> {
        logger.info("Call external service for user: $userId")
        val response = when (client) {
            "HttpURLConnection" -> callUsingHttpURLConnection(userId)
            "HttpClient" -> callUsingHttpClient(userId)
            "ApacheHttpClient" -> callUsingApacheHttpClient(userId)
            "OkHttp" -> callUsingOkHttp(userId)
            "Retrofit" -> callUsingRetrofit(userId)
            "Feign" -> callUsingFeign(userId)
            else -> throw IllegalStateException("Unknown client: $client")
        }
        logger.info("Response from external service for user: $userId: $response")
        return response.invoices
    }

    private fun callUsingHttpURLConnection(userId: String): UserInvoicesDto {
        val url = URL("http://localhost:8081/demo3/invoices/$userId")
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("accept", "application/json")
        val responseStream: InputStream = connection.inputStream
        return jacksonObjectMapper().readValue(responseStream, UserInvoicesDto::class.java)
    }

    private fun callUsingHttpClient(userId: String): UserInvoicesDto { // part of JDK
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder(
            URI.create("http://localhost:8081/demo3/invoices/$userId"))
            .header("accept", "application/json")
            .build()
        val response = client.send(request, JsonBodyHandler(UserInvoicesDto::class.java)) // additional class
        return response.body()
    }

    private fun callUsingApacheHttpClient(userId: String): UserInvoicesDto = // external lib
        HttpClients.createDefault().use { client ->
            val request = HttpGet("http://localhost:8081/demo3/invoices/$userId")
            return client.execute(request) { httpResponse -> jacksonObjectMapper().readValue(httpResponse.entity.content, UserInvoicesDto::class.java) }
        }

    private fun callUsingOkHttp(userId: String): UserInvoicesDto { // external lib
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://localhost:8081/demo3/invoices/$userId")
            .build()
        val response = client.newCall(request).execute()
        return jacksonObjectMapper().readValue(response.body?.byteStream(), UserInvoicesDto::class.java)
    }

    private fun callUsingRetrofit(userId: String): UserInvoicesDto { // external lib
        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val client = retrofit.create(ExternalServiceRetrofitClient::class.java) // additional interface
        return client.getUserInvoices(userId).get()
    }

    // external lib but provided as spring boot starter and external interface
    private fun callUsingFeign(userId: String): UserInvoicesDto = externalServiceFeignClient.getUserInvoicesDemo3(userId)
}

class JsonBodyHandler<T>(private val clazz: Class<T>) : BodyHandler<T> {

    override fun apply(responseInfo: ResponseInfo): BodySubscriber<T> = asJSON(clazz)

    private fun <T> asJSON(targetType: Class<T>): BodySubscriber<T> {
        val upstream = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8)
        return HttpResponse.BodySubscribers.mapping(upstream) { body -> return@mapping jacksonObjectMapper().readValue(body, targetType) }
    }
}

interface ExternalServiceRetrofitClient {
    @GET("/demo3/invoices/{userId}")
    fun getUserInvoices(@Path("userId") userId: String): CompletableFuture<UserInvoicesDto>
}

