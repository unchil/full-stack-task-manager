package org.example.ktor.data

import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.*
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import org.example.ktor.db.entity.ObservationTable
import org.example.ktor.db.entity.ObservatoryTable
import org.example.ktor.db.entity.observationTableToModel
import org.example.ktor.db.entity.observatoryTableToModel
import org.example.ktor.model.Observation
import org.example.ktor.model.Observatory
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class NifsRepository:NifsRepositoryInterface {

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, statement = block)


    @OptIn(FormatStringsInDatetimeFormats::class)
    override suspend fun lastOneDayObservation(): List<Observation> = suspendTransaction {

        val previous24Hour = Clock.System.now()
            .minus(24, DateTimeUnit.HOUR)
            .toLocalDateTime(TimeZone.of("Asia/Seoul"))
            .format(LocalDateTime.Format{byUnicodePattern("yyyy-MM-dd HH:mm:ss")})

        ObservationTable.selectAll()
            .where {
                ObservationTable.obs_datetime.greaterEq(previous24Hour)
            }.map {
                observationTableToModel(it)
            }
    }

    override suspend fun currentObservation(): List<Observation> = suspendTransaction {

        val currentDateTime = ObservationTable.obs_datetime.max()

        ObservationTable.selectAll()
            .where {
                ObservationTable.obs_datetime.eq(currentDateTime)
            }.map{
                observationTableToModel(it)
            }
    }

    override suspend fun observatoryInfo(): List<Observatory> = suspendTransaction {
        ObservatoryTable.selectAll()
            .map {
                observatoryTableToModel(it)
            }
    }


}