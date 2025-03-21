package org.example.ktor.data

import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.*
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import org.example.ktor.db.entity.ObservationTable
import org.example.ktor.db.entity.ObservatoryTable
import org.example.ktor.db.entity.toObservation
import org.example.ktor.db.entity.toObservatory
import org.example.ktor.db.entity.toSeawaterInformationByObservationPoint
import org.example.ktor.model.Observation
import org.example.ktor.model.Observatory
import org.example.ktor.model.SeawaterInformationByObservationPoint
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class NifsRepository:NifsRepositoryInterface {

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, statement = block)


    @OptIn(FormatStringsInDatetimeFormats::class)
    override suspend fun seaWaterInfo(division: String): List<SeawaterInformationByObservationPoint>  = suspendTransaction {

        return@suspendTransaction when(division) {
            "oneday" -> {
                val previous24Hour = Clock.System.now()
                    .minus(24, DateTimeUnit.HOUR)
                    .toLocalDateTime(TimeZone.of("Asia/Seoul"))
                    .format(LocalDateTime.Format{byUnicodePattern("yyyy-MM-dd HH:mm:ss")})

                ObservationTable.join(
                    ObservatoryTable,
                    JoinType.LEFT,
                    onColumn = ObservationTable.sta_cde,
                    otherColumn = ObservatoryTable.sta_cde
                ).select( ObservationTable.sta_cde,
                    ObservationTable.sta_nam_kor,
                    ObservationTable.obs_datetime,
                    ObservationTable.obs_lay,
                    ObservationTable.wtr_tmp,
                    ObservationTable.dox,
                    ObservationTable.sal,
                    ObservatoryTable.gru_nam,
                    ObservatoryTable.lat,
                    ObservatoryTable.lon
                ).where{
                    ObservationTable.obs_datetime greaterEq previous24Hour
                }.map {
                    toSeawaterInformationByObservationPoint(it)
                }
            }

            "current" -> {
                val lastTime = ObservationTable.obs_datetime.max()
                val currentTime = ObservationTable.select(lastTime).limit(1)
                    .map {
                        it[lastTime].toString()
                    }.last()

                ObservationTable.join(
                    ObservatoryTable,
                    JoinType.LEFT,
                    onColumn = ObservationTable.sta_cde,
                    otherColumn = ObservatoryTable.sta_cde
                ).select( ObservationTable.sta_cde,
                    ObservationTable.sta_nam_kor,
                    ObservationTable.obs_datetime,
                    ObservationTable.obs_lay,
                    ObservationTable.wtr_tmp,
                    ObservationTable.dox,
                    ObservationTable.sal,
                    ObservatoryTable.gru_nam,
                    ObservatoryTable.lat,
                    ObservatoryTable.lon
                ).where{
                    ObservationTable.obs_datetime eq currentTime
                }.map {
                    toSeawaterInformationByObservationPoint(it)
                }
            }

            else -> {emptyList()}
        }



    }


    @OptIn(FormatStringsInDatetimeFormats::class)
    override suspend fun observationList(division: String): List<Observation> = suspendTransaction {

        return@suspendTransaction when(division) {
            "oneday" -> {
                val previous24Hour = Clock.System.now()
                    .minus(24, DateTimeUnit.HOUR)
                    .toLocalDateTime(TimeZone.of("Asia/Seoul"))
                    .format(LocalDateTime.Format{byUnicodePattern("yyyy-MM-dd HH:mm:ss")})

                ObservationTable.selectAll().where{
                    ObservationTable.obs_datetime greaterEq previous24Hour
                }.map {
                    toObservation(it)
                }
            }
            "current" -> {
                val lastTime = ObservationTable.obs_datetime.max()

                val currentTime = ObservationTable.select(lastTime).limit(1)
                    .map {
                        it[lastTime].toString()
                    }.last()


                ObservationTable.selectAll().where{
                    ObservationTable.obs_datetime eq currentTime
                }.map {
                   toObservation(it)
                }
            }
            else -> {
                emptyList()
            }
        }

    }


    override suspend fun observatoryInfo(): List<Observatory> = suspendTransaction {
        ObservatoryTable.selectAll()
            .map {
                toObservatory(it)
            }
    }


}