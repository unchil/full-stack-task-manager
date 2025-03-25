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
import org.example.ktor.model.SeaWaterInfoByOneHourStat
import org.example.ktor.model.SeawaterInformationByObservationPoint
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.statements.DeleteStatement.Companion.where
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.sql.Types.FLOAT

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
    override suspend fun seaWaterInfoStatistics(): List<SeaWaterInfoByOneHourStat>  = suspendTransaction {
        val previous24Hour = Clock.System.now()
            .minus(24, DateTimeUnit.HOUR)
            .toLocalDateTime(TimeZone.of("Asia/Seoul"))
            .format(LocalDateTime.Format{byUnicodePattern("yyyy-MM-dd HH:mm:ss")})

        val time = ObservationTable.obs_tim.substring(0,3)
        val datetime = ObservationTable.obs_datetime.min()
        val tmp_min = ObservationTable.wtr_tmp.castTo(FloatColumnType()).min()
        val tmp_max = ObservationTable.wtr_tmp.castTo(FloatColumnType()).max()
        val tmp_avg = ObservationTable.wtr_tmp.castTo(FloatColumnType()).avg()

            ObservationTable
                .join(
                    ObservatoryTable,
                    JoinType.LEFT,
                    onColumn = ObservationTable.sta_cde,
                    otherColumn = ObservatoryTable.sta_cde
                )
                .select(
                    ObservatoryTable.gru_nam,
                    ObservationTable.sta_cde,
                    ObservationTable.sta_nam_kor,
                    datetime,
                    tmp_min,
                    tmp_max,
                    tmp_avg
                )
                .where { (ObservationTable.obs_datetime greaterEq previous24Hour) and (ObservationTable.obs_lay eq "1")}
                .groupBy ( ObservatoryTable.gru_nam, ObservationTable.sta_cde , ObservationTable.sta_nam_kor, time)
                .orderBy( ObservatoryTable.gru_nam to SortOrder.ASC, ObservationTable.sta_nam_kor to SortOrder.ASC , datetime to SortOrder.ASC  )
                .map {
                    SeaWaterInfoByOneHourStat(
                        it[ObservatoryTable.gru_nam],
                        it[ObservationTable.sta_cde],
                        it[ObservationTable.sta_nam_kor],
                        it[datetime].toString(),
                        it[tmp_min].toString(),
                        it[tmp_max].toString(),
                        it[tmp_avg].toString()
                    )
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