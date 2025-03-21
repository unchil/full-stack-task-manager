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
import org.example.ktor.model.RealTimeObservation
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lastValue
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class NifsRepository:NifsRepositoryInterface {

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, statement = block)


    override suspend fun observationRealTime(): List<RealTimeObservation>  = suspendTransaction {

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
            RealTimeObservation(
                it[ObservationTable.sta_cde],
                it[ObservationTable.sta_nam_kor],
                it[ObservationTable.obs_datetime],
                it[ObservationTable.obs_lay],
                it[ObservationTable.wtr_tmp],
                it[ObservationTable.dox],
                it[ObservationTable.sal],
                it[ObservatoryTable.gru_nam],
                it[ObservatoryTable.lat],
                it[ObservatoryTable.lon]
            )
        }
    }


    @OptIn(FormatStringsInDatetimeFormats::class)
    override suspend fun observationList(division: String): List<RealTimeObservation> = suspendTransaction {
        return@suspendTransaction when(division){
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
                    RealTimeObservation(
                        it[ObservationTable.sta_cde],
                        it[ObservationTable.sta_nam_kor],
                        it[ObservationTable.obs_datetime],
                        it[ObservationTable.obs_lay],
                        it[ObservationTable.wtr_tmp],
                        it[ObservationTable.dox],
                        it[ObservationTable.sal],
                        it[ObservatoryTable.gru_nam],
                        it[ObservatoryTable.lat],
                        it[ObservatoryTable.lon]
                    )
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
                    RealTimeObservation(
                        it[ObservationTable.sta_cde],
                        it[ObservationTable.sta_nam_kor],
                        it[ObservationTable.obs_datetime],
                        it[ObservationTable.obs_lay],
                        it[ObservationTable.wtr_tmp],
                        it[ObservationTable.dox],
                        it[ObservationTable.sal],
                        it[ObservatoryTable.gru_nam],
                        it[ObservatoryTable.lat],
                        it[ObservatoryTable.lon]
                    )
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
                observatoryTableToModel(it)
            }
    }


}