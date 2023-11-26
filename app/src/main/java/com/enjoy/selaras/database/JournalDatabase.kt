package com.enjoy.selaras.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.enjoy.selaras.dao.JournalDao
import com.enjoy.selaras.database.migrations.MigrationFrom1To2
import com.enjoy.selaras.entities.Journal

@Database(entities = [Journal::class], version = 2, exportSchema = true)
abstract class JournalDatabase : RoomDatabase() {
    companion object {
        private var journalDatabase: JournalDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): JournalDatabase {
            val migrationFrom1To2: Migration = MigrationFrom1To2();
            if (journalDatabase == null) {
                journalDatabase =
                    Room.databaseBuilder(context, JournalDatabase::class.java, "journals.db")
                        .addMigrations(migrationFrom1To2).fallbackToDestructiveMigration()
                        .build()
            }

            return journalDatabase!!
        }
    }

    abstract fun journalDao(): JournalDao
}