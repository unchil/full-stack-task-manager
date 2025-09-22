package org.example.ktor.module

import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

fun Application.configureNifsDatabase() {

    val databaseName = environment.config.property("storage.dbName").getString()

    val database = with(databaseName) {
        when{
            startsWith("h2") -> {
                val driver = environment.config.property("storage.database.h2.driverClassName").getString()
                val url = environment.config.property("storage.database.h2.jdbcURL").getString()
                val user = environment.config.property("storage.database.h2.user").getString()
                val password = environment.config.property("storage.database.h2.password").getString()
                Database.connect(
                    url = url,
                    driver = driver,
                    user = user,
                    password = password
                )
            }
/*
### HikariCP의 HikariConfig

## 주요 성능 관련 설정
maximumPoolSize : 풀에 유지할 수 있는 최대 커넥션 수입니다. 이 값이 너무 크면 데이터베이스에 과부하를 줄 수 있고, 너무 작으면 대기 시간이 발생할 수 있습니다. 코어 수에 따라 적절하게 설정하는 것이 중요합니다.
minimumIdle : 풀에 유지할 최소 유휴(idle) 커넥션 수입니다. 풀이 이 수 이하로 떨어지면 HikariCP는 새로운 커넥션을 생성하여 이 값을 유지합니다. 이 값이 maximumPoolSize와 같으면 유휴 커넥션이 없도록 항상 풀이 가득 차게 됩니다.
idleTimeout : 풀에 있는 유휴 커넥션을 유지하는 최대 시간(밀리초)입니다. minimumIdle이 maximumPoolSize보다 작을 때만 의미가 있습니다. HikariCP는 이 시간이 지나면 유휴 커넥션을 닫습니다.
connectionTimeout : 클라이언트가 풀에서 커넥션을 얻기 위해 기다릴 최대 시간(밀리초)입니다. 이 시간 내에 커넥션을 얻지 못하면 예외가 발생합니다.
leakDetectionThreshold : 커넥션이 풀로 반환되지 않고 풀 밖에서 사용되는 시간을 감지하는 임계값(밀리초)입니다. 이 시간을 초과하면 경고 로그가 출력되어 커넥션 누수를 파악할 수 있게 도와줍니다.

## 주요 안정성 및 관리 관련 설정
connectionTestQuery : 커넥션이 유효한지 확인하기 위해 사용되는 쿼리입니다. DB 종류에 따라 다릅니다. MySQL은 SELECT 1, PostgreSQL은 SELECT 1 또는 SELECT version() 등을 사용합니다.
autoCommit : 트랜잭션의 자동 커밋 여부를 설정합니다. false로 설정하고 수동으로 커밋/롤백을 관리하는 것이 일반적입니다.
allowPoolSuspension : 풀이 일시 중단될 수 있도록 허용합니다. 풀이 일시 중단되면 모든 커넥션 체크아웃 요청은 차단됩니다.
transactionIsolation : 풀에서 얻는 커넥션의 기본 트랜잭션 격리 수준을 설정합니다. TRANSACTION_REPEATABLE_READ, TRANSACTION_SERIALIZABLE 등을 사용합니다.
 */
            startsWith("sqlite") -> {
                val config = HikariConfig().apply {
                    jdbcUrl = environment.config.property("storage.database.sqlite.jdbcURL").getString()
                    driverClassName = environment.config.property("storage.database.sqlite.driverClassName").getString()
                    maximumPoolSize = 10
                    isAutoCommit = false
                    validate()
                }
                val dataSource = HikariDataSource(config)
                Database.connect(dataSource)
            }
            else -> {
                val driver = environment.config.property("storage.database.h2.driverClassName").getString()
                val url = environment.config.property("storage.database.h2.jdbcURL").getString()
                val user = environment.config.property("storage.database.h2.user").getString()
                val password = environment.config.property("storage.database.h2.password").getString()
                Database.connect(
                    url = url,
                    driver = driver,
                    user = user,
                    password = password
                )

            }
        }
    }

    fun initMemoryDb(db:Database){
        transaction(db) {
            addLogger(DBSqlLogger)
        }
    }

    fun initSqliteDbTable(db:Database){
        transaction (db){
            addLogger(DBSqlLogger)
        }
    }

    with(databaseName) {
        when {
            startsWith("h2") -> {
                initMemoryDb(database)
            }
            startsWith("sqlite") -> {
                initSqliteDbTable(database)
            }
            else -> {
                initMemoryDb(database)
            }
        }

    }
}

