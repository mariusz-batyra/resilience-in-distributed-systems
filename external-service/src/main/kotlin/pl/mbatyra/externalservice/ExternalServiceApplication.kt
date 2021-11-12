package pl.mbatyra.externalservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExternalServiceApplication

fun main(args: Array<String>) {
	runApplication<ExternalServiceApplication>(*args)
}
