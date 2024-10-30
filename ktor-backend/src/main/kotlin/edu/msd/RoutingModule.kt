package edu.msd

import io.ktor.http.*
import io.ktor.http.content.*

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

fun Application.routingModule() {
    routing {
        post("/uploadDrawing") {
            val multipart = call.receiveMultipart()
            var filePath: String? = null
            var color: Int? = null
            var brushSize: Float? = null
            var userId: String? = null

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
                        val file = File("uploads/$originalFileName")
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output -> input.copyTo(output) }
                        }
                        filePath = file.absolutePath
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (filePath != null && color != null && brushSize != null && userId != null) {
                transaction {
                    DrawingsTable.insert {
                        it[DrawingsTable.filePath] = filePath!!
                        it[DrawingsTable.color] = color!!
                        it[DrawingsTable.brushSize] = brushSize!!
                        it[DrawingsTable.date] = System.currentTimeMillis()
                    }
                }
                call.respond(HttpStatusCode.OK, message = "File uploaded successfully")
            } else {
                call.respond(HttpStatusCode.BadRequest, message = "Missing required fields")
            }
        }
        delete("/drawings/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                return@delete
            }

            val deletedCount = transaction {
                DrawingsTable.deleteWhere { DrawingsTable.id eq id }
            }

            if (deletedCount > 0) {
                call.respond(HttpStatusCode.OK, "Drawing deleted successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Drawing not found")
            }
        }
        get("/drawings/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                return@get
            }

            val drawing = transaction {
                DrawingsTable.select { DrawingsTable.id eq id }
                    .map {
                        mapOf(
                            "id" to it[DrawingsTable.id].value,
                            "filePath" to it[DrawingsTable.filePath],
                            "color" to it[DrawingsTable.color],
                            "brushSize" to it[DrawingsTable.brushSize],
                            "date" to it[DrawingsTable.date]
                        )
                    }
                    .singleOrNull()
            }

            if (drawing != null) {
                call.respond(drawing)
            } else {
                call.respond(HttpStatusCode.NotFound, "Drawing not found")
            }
        }

        get("/drawings") {
            val drawings = transaction {
                DrawingsTable.selectAll().map {
                    mapOf(
                        "id" to it[DrawingsTable.id].value,
                        "filePath" to it[DrawingsTable.filePath],
                        "color" to it[DrawingsTable.color],
                        "brushSize" to it[DrawingsTable.brushSize],
                        "date" to it[DrawingsTable.date]
                    )
                }
            }
            call.respond(drawings)
        }
    }
}
