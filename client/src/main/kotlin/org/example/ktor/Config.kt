package org.example.ktor

import org.jetbrains.exposed.sql.Database
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.io.readJson

class Config {
    companion object {

        val config_df = DataRow.readJson(path= this::class.java.classLoader?.getResource("application.json")!!.path)
        val url = (config_df["SQLITE_DB"] as DataRow<*>)["jdbcURL"].toString()
        val driver = (config_df["SQLITE_DB"] as DataRow<*>)["driverClassName"].toString()

        val conn = Database.connect(
            url = url,
            driver = driver,
        )

        val jobType = (config_df["COLLECTION_TYPE"] as DataRow<*>)["type"].toString()
        val interval = (config_df["COLLECTION_TYPE"] as DataRow<*>)["interval"].toString()
    }
}