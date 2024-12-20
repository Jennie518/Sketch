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
import kotlinx.serialization.Serializable

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DBSettings {
    fun init() {
        // Initialize the H2 database
        Database.connect("jdbc:h2:mem:test;MODE=MYSQL;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
//        Database.connect("jdbc:h2:file:./data/db;MODE=MYSQL", driver = "org.h2.Driver")


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

@Serializable
data class SimpleResponse(
    val message: String,
    val drawingId: Int
)
@Serializable
data class Drawing(
    val id: Int,
    val filePath: String,
    val color: Int,
    val brushSize: Float,
    val date: Long,
    val userId: String
)

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
            println("before save to db")
            if (filePath != null && color != null && brushSize != null && userId != null) {
                println("insert to db")
                val drawingId = transaction {
                    DrawingsTable.insert {
                        it[DrawingsTable.filePath] = filePath!!
                        it[DrawingsTable.color] = color!!
                        it[DrawingsTable.brushSize] = brushSize!!
                        it[DrawingsTable.date] = System.currentTimeMillis()
                        it[DrawingsTable.userId] = userId!!
                    } get DrawingsTable.id
                }.value
                    .also {
                    println("Inserted Drawing ID: $it")
                }

                call.respond(HttpStatusCode.OK, SimpleResponse("File uploaded successfully", drawingId))
            } else {
                call.respond(HttpStatusCode.BadRequest, message = "Missing required fields")
            }
        }
//        get drawing by id
        get("/drawings/{id}") {
            val drawingId = call.parameters["id"]?.toIntOrNull()

            if (drawingId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                return@get
            }

            val drawingPath = transaction {
                DrawingsTable.select { DrawingsTable.id eq drawingId }
                    .map { it[DrawingsTable.filePath] }
                    .singleOrNull()
            }

            if (drawingPath != null) {
                val file = File(drawingPath)
                if (file.exists()) {
                    call.respondFile(file)  // Respond with the actual image file
                } else {
                    call.respond(HttpStatusCode.NotFound, "File not found")
                }
            } else {
                call.respond(HttpStatusCode.NotFound, "Drawing not found")
            }
        }
//        unShared logic
        delete("/drawings/{id}") {
            val userId = call.request.headers["userId"]
            val drawingId = call.parameters["id"]?.toIntOrNull()

            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
                return@delete
            }

            if (drawingId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing drawing ID")
                return@delete
            }

            call.application.log.info("Attempting to delete drawing with ID: $drawingId for user ID: $userId")

            val drawingRecord = transaction {
                DrawingsTable.select { (DrawingsTable.id eq drawingId) and (DrawingsTable.userId eq userId) }
                    .map { it[DrawingsTable.filePath] }
                    .singleOrNull()
            }

            if (drawingRecord != null) {
                val file = File(drawingRecord)
                val fileDeleted = file.exists() && file.delete()

                if (fileDeleted) {
                    call.application.log.info("File deleted successfully: ${file.absolutePath}")
                } else {
                    call.application.log.warn("File not found or failed to delete: ${file.absolutePath}")
                }

                val deletedCount = transaction {
                    DrawingsTable.deleteWhere {
                        (DrawingsTable.id eq drawingId) and (DrawingsTable.userId eq userId)
                    }
                }

                if (deletedCount > 0) {
                    call.respond(HttpStatusCode.OK, "Drawing and file deleted successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Drawing not found or unauthorized")
                }
            } else {
                call.respond(HttpStatusCode.NotFound, "Drawing not found or unauthorized")
            }
        }
    }
    log.info("Ktor application started successfully with routes.")
}

