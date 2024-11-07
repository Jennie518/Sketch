package edu.msd.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.serialization.kotlinx.xml.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
//        json()
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true // 如果有前端不需要的字段，这个配置会忽略它们
        })
        xml()

    }
    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
            //creates a json object: {"hello": "world"}
        }
    }
}
