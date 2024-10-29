//package edu.msd
//
//import org.jetbrains.exposed.dao.id.IntIdTable
//import org.jetbrains.exposed.sql.Table
//
//
//object User : IntIdTable() {} //we have just one field, ID, primary, int, auto increment
//
//object Book: IntIdTable(){
//    val title = varchar("title", 100).uniqueIndex()
//}
//
//object Post: IntIdTable() {
//    val content = varchar("content", 255)
////    for post time
//    val timestamp = long("timestamp")
//
//}
//
//object Likes: Table(){
//    val user = reference("user_id", User.id)
//    val book = reference("book_id", Book.id)
//    override val primaryKey = PrimaryKey(arrayOf(user, book))
//
//}
//
