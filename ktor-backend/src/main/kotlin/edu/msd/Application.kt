//package edu.msd

import edu.msd.DrawingsTable
import edu.msd.plugins.*
import io.ktor.http.*
import io.ktor.http.content.*

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DBSettings {
    fun init() {
        // Initialize the H2 database
        Database.connect("jdbc:h2:mem:test;MODE=MYSQL;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

        // Execute table creation in a transaction block
        transaction {
            SchemaUtils.create(DrawingsTable)
        }
    }
}


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DBSettings.init()
//    configureSecurity()
//    configureHTTP()
//    configureMonitoring()
    configureSerialization()
//    routingModule()
//    configureRouting()
    log.info("Ktor application started successfully with uploadDrawing route.")
    routing {
            get("/hello") {
                call.respondText("Hello World!", ContentType.Text.Plain)
            }
            post("/uploadDrawing") {
                val multipart = call.receiveMultipart()
                var filePath: String? = null
                var color: Int? = null
                var brushSize: Float? = null
                var userId: String? = null

                try {
                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                when (part.name) {
                                    "color" -> color = part.value.toIntOrNull()
                                    "brushSize" -> brushSize = part.value.toFloatOrNull()
                                    "userId" -> userId = part.value
                                }
                            }
                            is PartData.FileItem -> {
                                val originalFileName = part.originalFileName ?: return@forEachPart
                                val uploadsDir = File("uploads")
                                if (!uploadsDir.exists()) {
                                    uploadsDir.mkdirs()
                                }
                                val file = File(uploadsDir, originalFileName)
                                part.streamProvider().use { input ->
                                    file.outputStream().buffered().use { output -> input.copyTo(output) }
                                }
                                filePath = file.absolutePath
                            }
                            else -> {}
                        }
                        part.dispose()
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Server error occurred while processing the request")
                    return@post
                }

                if (filePath != null && color != null && brushSize != null && userId != null) {
                    transaction {
                        DrawingsTable.insert {
                            it[DrawingsTable.filePath] = filePath!!
                            it[DrawingsTable.color] = color!!
                            it[DrawingsTable.brushSize] = brushSize!!
                            it[DrawingsTable.date] = System.currentTimeMillis()
                            it[DrawingsTable.userId] = userId!!
                        }
                    }
                    call.respond(HttpStatusCode.OK, message = "File uploaded successfully")
                } else {
                    call.respond(HttpStatusCode.BadRequest, message = "Missing required fields")
                }
            }
    }
    log.info("Ktor application started successfully with routes.")
}

