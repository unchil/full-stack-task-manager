package org.example.ktor

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnGroup
import org.jetbrains.kotlinx.dataframe.io.readJson

class Config {

    companion object {
        private val NIFS_API by columnGroup()
        private val id by NIFS_API.columnGroup()
        private val SQLITE_DB by columnGroup()

        val endPoint by NIFS_API.column<String>()
        val apikey by NIFS_API.column<String>()
        val subPath by NIFS_API.column<String>()
        val list by id.column<String>()
        val code by id.column<String>()
        val driverClassName  by SQLITE_DB.column<String>()
        val jdbcURL by SQLITE_DB.column<String>()

        val Item = DataRow.readJson(path= this::class.java.classLoader?.getResource("application.json")!!.path)

    }
}