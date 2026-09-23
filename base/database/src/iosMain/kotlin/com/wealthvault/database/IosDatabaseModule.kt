package com.wealthvault.database

import app.cash.sqldelight.db.SqlDriver
import org.koin.dsl.module

val iosDatabaseModule = module {
    single { DatabaseDriverFactory() }
    single<SqlDriver> { get<DatabaseDriverFactory>().createDriver() }
}
