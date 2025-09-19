package org.example.ktor.module

import io.ktor.util.logging.KtorSimpleLogger
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs

val LOGGER = KtorSimpleLogger( "FullStackTaskManager")

object DBSqlLogger : SqlLogger {
    val LOGGER = KtorSimpleLogger( "")
    override fun log(context: StatementContext, transaction: Transaction) {
        if (LOGGER.isErrorEnabled) {
            LOGGER.error("SQL: ${context.expandArgs(transaction)}")
        }
        if (LOGGER.isWarnEnabled) {
            LOGGER.warn("SQL: ${context.expandArgs(transaction)}")
        }
        if (LOGGER.isInfoEnabled) {
            LOGGER.info("SQL: ${context.expandArgs(transaction)}")
        }
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("SQL: ${context.expandArgs(transaction)}")
        }
        if (LOGGER.isTraceEnabled) {
            LOGGER.trace("SQL: ${context.expandArgs(transaction)}")
        }
    }
}
