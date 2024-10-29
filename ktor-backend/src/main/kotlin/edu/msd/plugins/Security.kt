package edu.msd.plugins

import edu.msd.DBSettings
import edu.msd.User
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.concurrent.atomic.AtomicInteger

data class SessionData(val sessionID: Int)
private var nextID = AtomicInteger(0)
suspend fun getSessionData(currentSession: CurrentSession): SessionData {
    val curr = currentSession.get<SessionData>()
    if (curr == null) {
        val newSession = SessionData(nextID.incrementAndGet())
        currentSession.set(newSession)
        newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
            User.insert {
                it[id] = newSession.sessionID
            }
        }
        return newSession
    }
    return curr
}

fun Application.configureSecurity() {


    install(Sessions) {
        header<SessionData>("MY_SESSION", SessionStorageMemory())
    }

}


