package com.enjoy.selaras.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class MigrationFrom1To2 : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
       db.execSQL("ALTER TABLE journals ADD COLUMN emotion TEXT")
    }
}