package org.example.ktor.data

import io.ktor.server.response.respond
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.*
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import org.example.ktor.db.entity.*
import org.example.ktor.model.Observation
import org.example.ktor.model.Observatory
import org.example.ktor.model.SeaWaterInfoByOneHourStat
import org.example.ktor.model.SeawaterInformationByObservationPoint
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

// 캐시를 저장할 ConcurrentHashMap. 스레드 안전성을 보장합니다.
private val cache_SeawaterInfo = ConcurrentHashMap<String, Pair<List<SeawaterInformationByObservationPoint>, Long>>()
private val cache_SeaWaterInfoStatistics = ConcurrentHashMap<String, Pair<List<SeaWaterInfoByOneHourStat>, Long>>()
private const val CACHE_EXPIRY_SECONDS =  10 * 60L  // 10분




class NifsRepository:NifsRepositoryInterface {

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, statement = block)


    @OptIn(FormatStringsInDatetimeFormats::class)
    override suspend fun seaWaterInfo(division: String): List<SeawaterInformationByObservationPoint>  = suspendTransaction {

        val key_SeawaterInfo = "cache_$division"
        val now = System.currentTimeMillis()

        // 캐시에서 데이터 조회
        cache_SeawaterInfo[key_SeawaterInfo]?.let { it ->
            if( (now - it.second) < TimeUnit.SECONDS.toMillis(CACHE_EXPIRY_SECONDS) ){
                println("Serving from cache for ID: $division")
                return@suspendTransaction it.first
            }
        }

        val result = when(division) {
            "oneday" -> {
                val previous24Hour = Clock.System.now()
                    .minus(24, DateTimeUnit.HOUR)
                    .toLocalDateTime(TimeZone.of("Asia/Seoul"))
                    .format(LocalDateTime.Format{byUnicodePattern("yyyy-MM-dd HH:mm:ss")})

                ObservationTable.join(
                    ObservatoryTable,
                    JoinType.INNER,
                    onColumn = ObservationTable.sta_cde,
                    otherColumn = ObservatoryTable.sta_cde
                ).select( ObservationTable.sta_cde,
                    ObservationTable.sta_nam_kor,
                    ObservationTable.obs_datetime,
                    ObservationTable.obs_lay,
                    ObservationTable.wtr_tmp,
                    ObservatoryTable.gru_nam,
                    ObservatoryTable.lon,
                    ObservatoryTable.lat

                ).where{
                    ObservationTable.obs_datetime greaterEq previous24Hour
                }.map {
                    toSeawaterInformationByObservationPoint(it)
                }
            }
            "grid" -> {
                val previous24Hour = Clock.System.now()
                    .minus(24, DateTimeUnit.HOUR)
                    .toLocalDateTime(TimeZone.of("Asia/Seoul"))
                    .format(LocalDateTime.Format{byUnicodePattern("yyyy-MM-dd HH:mm:ss")})

                ObservationTable.join(
                    ObservatoryTable,
                    JoinType.INNER,
                    onColumn = ObservationTable.sta_cde,
                    otherColumn = ObservatoryTable.sta_cde
                ).select( ObservationTable.sta_cde,
                    ObservationTable.sta_nam_kor,
                    ObservationTable.obs_datetime,
                    ObservationTable.obs_lay,
                    ObservationTable.wtr_tmp,
                    ObservatoryTable.gru_nam,
                    ObservatoryTable.lon,
                    ObservatoryTable.lat

                ).where{
                    ObservationTable.obs_datetime greaterEq previous24Hour
                }.orderBy( ObservationTable.obs_datetime to SortOrder.DESC).map {
                        toSeawaterInformationByObservationPoint(it)
                }
            }

            "current" -> {
                val lastTime = ObservationTable.obs_datetime.max()
                val currentTime = ObservationTable.select(lastTime).limit(1).map {
                    it[lastTime].toString()
                }.last()

                ObservationTable.join(
                    ObservatoryTable,
                    JoinType.INNER,
                    onColumn = ObservationTable.sta_cde,
                    otherColumn = ObservatoryTable.sta_cde
                ).select( ObservationTable.sta_cde,
                    ObservationTable.sta_nam_kor,
                    ObservationTable.obs_datetime,
                    ObservationTable.obs_lay,
                    ObservationTable.wtr_tmp,
                    ObservatoryTable.gru_nam,
                    ObservatoryTable.lon,
                    ObservatoryTable.lat

                ).where{
                    ObservationTable.obs_datetime eq currentTime
                }.map {
                    toSeawaterInformationByObservationPoint(it)
                }
            }

            else -> {emptyList()}
        }
        // 새로운 데이터를 캐시에 저장
        cache_SeawaterInfo[key_SeawaterInfo] = Pair(result, now)
        return@suspendTransaction result
    }


    @OptIn(FormatStringsInDatetimeFormats::class)
    override suspend fun seaWaterInfoStatistics(): List<SeaWaterInfoByOneHourStat>  = suspendTransaction {

        val key_SeaWaterInfoStatistics = "cache_stat"
        val now = System.currentTimeMillis()
        cache_SeaWaterInfoStatistics[key_SeaWaterInfoStatistics]?.let {
            if( (now - it.second) < TimeUnit.SECONDS.toMillis(CACHE_EXPIRY_SECONDS) ){
                println("Serving from cache for ID: stat")
                return@suspendTransaction it.first
            }
        }

        val previous24Hour = Clock.System.now()
            .minus(24, DateTimeUnit.HOUR)
            .toLocalDateTime(TimeZone.of("Asia/Seoul"))
            .format(LocalDateTime.Format{byUnicodePattern("yyyy-MM-dd HH:mm:ss")})

        val time = ObservationTable.obs_tim.substring(0,3)
        val datetime = ObservationTable.obs_datetime.min().substring(3, 11)
        val tmp_min = ObservationTable.wtr_tmp.castTo(FloatColumnType()).min()
        val tmp_max = ObservationTable.wtr_tmp.castTo(FloatColumnType()).max()
        val tmp_avg = ObservationTable.wtr_tmp.castTo(FloatColumnType()).avg()

        val result = ObservationTable
                .join(
                    ObservatoryTable,
                    JoinType.INNER,
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

        cache_SeaWaterInfoStatistics[key_SeaWaterInfoStatistics] = Pair(result, now)
        return@suspendTransaction result
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
                }
                .map {
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
                }
                .map {
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