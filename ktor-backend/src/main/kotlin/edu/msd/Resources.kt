//package edu.msd
//
//
//import edu.msd.Book.id
//import edu.msd.Book.title
//import edu.msd.plugins.getSessionData
//import io.ktor.http.*
//import io.ktor.resources.*
//import io.ktor.server.application.*
//import io.ktor.server.request.*
//import io.ktor.server.resources.*
//import io.ktor.server.resources.Resources
//import io.ktor.server.response.*
//import io.ktor.server.routing.routing
//
//import io.ktor.server.sessions.*
//import kotlinx.coroutines.Dispatchers
//import kotlinx.serialization.Serializable
//import org.jetbrains.exposed.dao.id.EntityID
//import org.jetbrains.exposed.sql.*
//import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
//import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
//import org.jetbrains.exposed.sql.transactions.transaction
//
//fun Application.configureResources() {
//    install(Resources)
//    routing{
//        post<Posts> {
//            val postContent = call.receiveText()
//            transaction {
//                Post.insert {
//                    it[content] = postContent
//                    it[timestamp] = System.currentTimeMillis()
//                }
//            }
//
//            call.respond(HttpStatusCode.Created, "Post created")
//        }
//        get<Posts.All> {
//            val posts = newSuspendedTransaction {
//                Post.selectAll().map {
//                    "ID: ${it[Post.id]}, Content: ${it[Post.content]}, Timestamp: ${it[Post.timestamp]}"
//                }
//            }
//            call.respondText(posts.joinToString(separator = "\n"))
//        }
//
//        get<Posts.Since> {
//            val since = call.request.queryParameters["since"]?.toLongOrNull()
//            if (since != null) {
//                val posts = newSuspendedTransaction {
//                    Post.select { Post.timestamp greaterEq since }.map {
//                        "ID: ${it[Post.id]}, Content: ${it[Post.content]}, Timestamp: ${it[Post.timestamp]}"
//                    }
//                }
//                call.respondText(posts.joinToString(separator = "\n"))
//            } else {
//                call.respond(HttpStatusCode.BadRequest, "Invalid 'since' parameter")
//            }
//        }
//
//
//        get<Posts.Id> {
//            val id = it.id
//            val post = newSuspendedTransaction {
//                Post.select { Post.id eq id }.singleOrNull()
//            }
//            if (post != null) {
//                call.respondText("ID: ${post[Post.id]}, Content: ${post[Post.content]}, Timestamp: ${post[Post.timestamp]}")
//            } else {
//                call.respond(HttpStatusCode.NotFound, "Post not found")
//            }
//        }
//        delete<Posts.Id> {
//            val id = it.id
//            val rowsDeleted = newSuspendedTransaction {
//                Post.deleteWhere { Post.id eq id }
//            }
//            if (rowsDeleted > 0) {
//                call.respondText("Post with ID: $id deleted")
//            } else {
//                call.respond(HttpStatusCode.NotFound, "Post not found")
//            }
//        }
//        get<Books> {
//            //handler for /books
//            call.respond(
//                newSuspendedTransaction(Dispatchers.IO) {
//                    Book.slice(Book.title)
//                        .selectAll()
//                        .map{it[Book.title]}
//
//                }
//            )
//        }
//
//        post<Books.Like> {
//            val titleInput = call.receive<BookPostData>().title
//            val userID = call.request.queryParameters["userID"]?.toIntOrNull() // 使用 queryParameters 获取 userID
//            if (userID == null) {
//                call.respond(HttpStatusCode.BadRequest, "Missing or invalid user ID")
//                return@post
//            }
//            newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
//                var bookID = Book.select(title eq titleInput).singleOrNull()?.let { it[Book.id] }
//                if (bookID == null) {
//                    bookID = Book.insert {
//                        it[title] = titleInput
//                    }[Book.id]
//                }
//                Likes.insertIgnore {
//                    it[book] = bookID as EntityID<Int>
//                    it[user] = userID
//                }
//            }
//            call.respondText("Liked $titleInput")
//        }
//
////
////        post<Books.Like> {
////            val userID = getSessionData(call.sessions).sessionID
////            val titleInput = call.receive<BookPostData>().title
////            newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
////                //returns null, inexplicably!
//////                val bookID = Book.insertIgnoreAndGetId{
//////                    it[title] = titleInput
//////                }!!
////
////                //this is bad because I couldn't figure out non-primary key constraints
////                var bookID = Book.select(title eq titleInput).singleOrNull()?.let{ it[Book.id]}
////                if(bookID == null) {
////                    bookID = Book.insert {
////                        it[title] = titleInput
////                    }[Book.id]
////                }
////                Likes.insertIgnore {
////                    it[book] = bookID
////                    it[user] = userID
////                }
////            }
////            call.respondText("Liked $titleInput")
////        }
//
//    }
//}
//
//
//@Serializable data class BookPostData(val title: String)
//@Resource("/books") //corresponds to /books
//class Books {
//    @Resource("liked") //corresponds to /books/liked
//    class Liked(val parent: Books = Books())
//
//    @Resource("like") //corresponds to /books/like?title=some%20book
//    class Like(val parent: Books = Books(), val title: String? = "")
//
//    @Resource("hate") //corresponds to /books/{id}/hate
//    class Hate(val parent: Books = Books(), val title: String? = "")
//}
//@Resource("/posts")
//class Posts{
//    @Resource("{id}")
//    class Id(val parent: Posts = Posts(), val id: Int)
//    @Resource("")
//    class All(val parent: Posts = Posts())
//
//    @Resource("since")
//    class Since(val parent: Posts = Posts(), val since: Long)
//
//}