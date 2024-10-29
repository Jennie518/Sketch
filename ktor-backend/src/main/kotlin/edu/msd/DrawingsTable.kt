package edu.msd

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.dao.id.IntIdTable

object DrawingsTable : IntIdTable() {
    val filePath = varchar("filePath", 255)
    val color = integer("color")
    val brushSize = float("brushSize")
    val date = long("date")
}
