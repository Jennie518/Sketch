package edu.msd

import edu.msd.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class ApplicationTest {
    @BeforeTest
    fun setup() {
        DBSettings.init()
    }
//
//    @Test
//    fun testRoot() = testApplication {
//        application {
//            configureRouting()
//        }
//        client.get("/").apply {
//            assertEquals(HttpStatusCode.OK, status)
//            assertEquals("Hello World!", bodyAsText())
//        }
//    }
//
//    @Test
//    fun testCreatePost() = testApplication {
//        application {
//            configureResources()
//        }
//
//        val response = client.post("/posts") {
//            setBody("This is a test post")
//        }
//        assertEquals(HttpStatusCode.Created, response.status)
//        assertEquals("Post created", response.bodyAsText())
//    }
//    @Test
//    fun testGetAllPosts() = testApplication {
//        application {
//            configureResources()
//        }
//
//        client.post("/posts") {
//            setBody("Test post content")
//        }
//
//        val response = client.get("/posts")
//        assertEquals(HttpStatusCode.OK, response.status)
//        assertTrue(response.bodyAsText().contains("Test post content"))
//    }
//
//
//    @Test
//    fun testGetPostsSince() = testApplication {
//        application {
//            configureRouting()
//            configureResources()  // Ensure Resources plugin is installed
//        }
//
//        newSuspendedTransaction {
//            Post.insert {
//                it[content] = "Test post content"
//                it[timestamp] = 1634554805000
//            }
//        }
//
//
//        client.get("/posts/since?since=1634554800000").apply {
//            assertEquals(HttpStatusCode.OK, status)
//            val body = bodyAsText()
//            println(body)
//            assertTrue(body.contains("ID:"))
//            assertTrue(body.contains("Content:"))
//        }
//    }
//
//
//
//    @Test
//    fun testDeletePost() = testApplication {
//        application {
//            configureResources()
//        }
//
//        val postId = newSuspendedTransaction {
//            Post.insertAndGetId {
//                it[content] = "This is a test post"
//                it[timestamp] = System.currentTimeMillis()
//            }
//        }
//
//        val response = client.delete("/posts/${postId.value}")
//        assertEquals(HttpStatusCode.OK, response.status)
//        assertEquals("Post with ID: ${postId.value} deleted", response.bodyAsText())
//    }
//
//    @Test
//    fun testGetBooks() = testApplication {
//        application {
//            configureResources()
//            configureSerialization()
//        }
//        newSuspendedTransaction {
//            Book.insert {
//                it[title] = "Kotlin Programming"
//            }
//            Book.insert {
//                it[title] = "Ktor in Action"
//            }
//        }
//        val response = client.get("/books")
//        assertEquals(HttpStatusCode.OK, response.status)
//        assertTrue(response.bodyAsText().contains("Kotlin Programming"))
//        assertTrue(response.bodyAsText().contains("Ktor in Action"))
//    }
//
//    @Test
//    fun testPostLikeBook() = testApplication {
//        application {
//            configureResources()
//            configureSerialization()
//        }
//        val bookId = newSuspendedTransaction {
//            Book.insertAndGetId {
//                it[title] = "Likeable Book"
//            }.value
//        }
//        val userId = newSuspendedTransaction {
//            User.insertAndGetId {}.value
//        }
//        val likeResponse = client.post("/books/like") {
//            setBody("""{"title": "Likeable Book"}""")
//            parameter("userID", userId)
//            contentType(ContentType.Application.Json)
//        }
//        assertEquals(HttpStatusCode.OK, likeResponse.status)
//        assertTrue(likeResponse.bodyAsText().contains("Liked Likeable Book"))
//    }
//}
//
//@AfterTest
//fun tearDown() {
//    transaction {
//        SchemaUtils.drop(Post, Book, Likes)
//    }
//}