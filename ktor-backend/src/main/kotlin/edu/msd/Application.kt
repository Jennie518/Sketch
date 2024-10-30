//package edu.msd

import edu.msd.DrawingsTable
import edu.msd.plugins.*

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

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
    configureHTTP()
//    configureMonitoring()
    configureSerialization()
//    configureRouting()
}

