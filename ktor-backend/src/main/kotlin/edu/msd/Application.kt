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
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
                            // Store in /uploads on the server
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
                val drawingId = transaction {
                    DrawingsTable.insert {
                        it[DrawingsTable.filePath] = filePath!!
                        it[DrawingsTable.color] = color!!
                        it[DrawingsTable.brushSize] = brushSize!!
                        it[DrawingsTable.date] = System.currentTimeMillis()
                        it[DrawingsTable.userId] = userId!!
                    } get DrawingsTable.id
                }

                call.respond(HttpStatusCode.OK, mapOf("message" to "File uploaded successfully", "drawingId" to drawingId))
            } else {
                call.respond(HttpStatusCode.BadRequest, message = "Missing required fields")
            }
        }
//            post("/uploadDrawing") {
//                val multipart = call.receiveMultipart()
//                var filePath: String? = null
//                var color: Int? = null
//                var brushSize: Float? = null
//                var userId: String? = null
//
//                try {
//                    multipart.forEachPart { part ->
//                        when (part) {
//                            is PartData.FormItem -> {
//                                when (part.name) {
//                                    "color" -> color = part.value.toIntOrNull()
//                                    "brushSize" -> brushSize = part.value.toFloatOrNull()
//                                    "userId" -> userId = part.value
//                                }
//                            }
//                            is PartData.FileItem -> {
//                                val originalFileName = part.originalFileName ?: return@forEachPart
////                                store in /uploads in the server
//                                val uploadsDir = File("uploads")
//                                if (!uploadsDir.exists()) {
//                                    uploadsDir.mkdirs()
//                                }
//                                val file = File(uploadsDir, originalFileName)
//                                part.streamProvider().use { input ->
//                                    file.outputStream().buffered().use { output -> input.copyTo(output) }
//                                }
//                                filePath = file.absolutePath
//                            }
//                            else -> {}
//                        }
//                        part.dispose()
//                    }
//                } catch (e: Exception) {
//                    call.respond(HttpStatusCode.InternalServerError, "Server error occurred while processing the request")
//                    return@post
//                }
//
//                if (filePath != null && color != null && brushSize != null && userId != null) {
//                    val drawingId = transaction {
//                        DrawingsTable.insert {
//                            it[DrawingsTable.filePath] = filePath!!
//                            it[DrawingsTable.color] = color!!
//                            it[DrawingsTable.brushSize] = brushSize!!
//                            it[DrawingsTable.date] = System.currentTimeMillis()
//                            it[DrawingsTable.userId] = userId!!
//                        }
//                    }
//                    call.respond(HttpStatusCode.OK, message = "File uploaded successfully with ID: $drawingId")
//                } else {
//                    call.respond(HttpStatusCode.BadRequest, message = "Missing required fields")
//                }
//            }
//        unShared logic
            delete("/drawings/{id}") {
                val userId = call.request.headers["userId"]
                val id = call.parameters["id"]?.toIntOrNull()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
                    return@delete
                }

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                    return@delete
                }

                val deletedCount = transaction {
                    DrawingsTable.deleteWhere {
                        (DrawingsTable.id eq id) and (DrawingsTable.userId eq userId) // 只能删除当前用户的绘图
                    }
                }

                if (deletedCount > 0) {
                    call.respond(HttpStatusCode.OK, "Drawing deleted successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Drawing not found or unauthorized")
                }
            }
    }
    log.info("Ktor application started successfully with routes.")
}

