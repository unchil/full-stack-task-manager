package org.example.ktor.data

import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.example.ktor.db.entity.ObservationTable
import org.example.ktor.db.entity.ObservatoryTable
import org.example.ktor.db.entity.toObservation
import org.example.ktor.db.entity.toObservatory
import org.example.ktor.db.entity.toSeawaterInformationByObservationPoint
import org.example.ktor.model.Observation
import org.example.ktor.model.Observatory
import org.example.ktor.model.SeaWaterInfoByOneHourStat
import org.example.ktor.model.SeawaterInformationByObservationPoint
import org.example.ktor.module.LOGGER
import org.jetbrains.exposed.sql.FloatColumnType
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.avg
import org.jetbrains.exposed.sql.castTo
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.min
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.substring
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit


// 캐시를 저장할 ConcurrentHashMap. 스레드 안전성을 보장합니다.
private val cacheStorage_SeawaterInfo = ConcurrentHashMap<String, Pair<List<SeawaterInformationByObservationPoint>, Long>>()
private val cacheStorage_SeaWaterInfoStatistics = ConcurrentHashMap<String, Pair<List<SeaWaterInfoByOneHourStat>, Long>>()
private const val CACHE_EXPIRY_SECONDS =  10 * 60L  // 10분




class NifsRepository:NifsRepositoryInterface {

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, statement = block)

    // 캐시 로직과 DB 조회 호출을 담당하는 메인 함수
    suspend fun seaWaterInfo(division: String): List<SeawaterInformationByObservationPoint> {
        val key = "cache_$division"
        val now = System.currentTimeMillis()

        // 캐시에서 데이터 조회 (suspendTransaction 외부)
        cacheStorage_SeawaterInfo[key]?.let { cachedData ->
            if ((now - cachedData.second) < TimeUnit.SECONDS.toMillis(CACHE_EXPIRY_SECONDS)) {
                LOGGER.info("Serving from cache for ID: $division")
                return cachedData.first
            }
        }
        // 캐시에 없거나 만료된 경우 DB에서 데이터 조회 (suspendTransaction 내부 호출)
        val resultFromDb = fetchSeaWaterInfoFromDb(division)
        if (resultFromDb.isNotEmpty() ) {
            cacheStorage_SeawaterInfo[key] = Pair(resultFromDb, now)
        }
        return resultFromDb
    }


    @OptIn(FormatStringsInDatetimeFormats::class)
    override suspend fun fetchSeaWaterInfoFromDb(division: String): List<SeawaterInformationByObservationPoint>  = suspendTransaction {
        LOGGER.info("Serving from DB for ID: $division")
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
                val lastTimeExpression = ObservationTable.obs_datetime.max()
                val currentTime = ObservationTable.select(lastTimeExpression).limit(1).map {
                    it[lastTimeExpression].toString()
                }.singleOrNull()

                if (currentTime == null) {
                    emptyList() // 현재 데이터가 없는 경우
                } else {
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

            }

            else -> {emptyList()}
        }
        return@suspendTransaction result
    }

    suspend fun seaWaterInfoStatistics(): List<SeaWaterInfoByOneHourStat>{
        val key = "cache_stat"
        val now = System.currentTimeMillis()
        cacheStorage_SeaWaterInfoStatistics[key]?.let { it ->
            if( (now - it.second) < TimeUnit.SECONDS.toMillis(CACHE_EXPIRY_SECONDS) ){
                LOGGER.info("Serving from cache for ID: stat")
                return it.first
            }
        }

        val resultFromDb = fetchSeaWaterInfoStatisticsFromDb()
        if (resultFromDb.isNotEmpty()) {
            cacheStorage_SeaWaterInfoStatistics[key] = Pair(resultFromDb, now)
        }
        return resultFromDb


    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    override suspend fun fetchSeaWaterInfoStatisticsFromDb(): List<SeaWaterInfoByOneHourStat>  = suspendTransaction {

        LOGGER.info("Serving from DB for ID: stat")
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