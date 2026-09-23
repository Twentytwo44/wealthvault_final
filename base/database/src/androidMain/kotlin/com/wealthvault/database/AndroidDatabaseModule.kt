package com.wealthvault.database

import app.cash.sqldelight.db.SqlDriver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidDatabaseModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single<SqlDriver> { get<DatabaseDriverFactory>().createDriver() }
}
