package com.enjoy.selaras.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.enjoy.selaras.dao.JournalDao
import com.enjoy.selaras.entities.Journal

@Database(entities = [Journal::class], version = 1, exportSchema = false)
abstract class JournalDatabase : RoomDatabase() {

    companion object {
        private var journalDatabase: JournalDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): JournalDatabase {
            if (journalDatabase == null) {
                journalDatabase =
                    Room.databaseBuilder(context, JournalDatabase::class.java, "journals.db")
                        .build()
            }

            return journalDatabase!!
        }
    }

    abstract fun journalDao(): JournalDao
}