package com.enjoy.selaras.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.enjoy.selaras.entities.Journal

@Dao
interface JournalDao {
    @Query("SELECT * FROM journals ORDER BY id DESC")
    suspend fun getAllJournal() : List<Journal>

    @Query("SELECT * FROM journals WHERE id = :id")
    suspend fun getSpecificJournal(id: Int) : Journal

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: Journal)

    @Delete
    suspend fun deleteJournal(journal: Journal)

    @Query("DELETE FROM journals WHERE id = :id")
    suspend fun deleteJournalById(id: Int)

    @Update
    suspend fun update(journal: Journal)
}