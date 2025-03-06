package com.lumisdinos.measureandcount.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lumisdinos.measureandcount.data.db.model.Chipboard

@Dao
interface ChipboardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChipboard(chipboard: Chipboard)

    @Query("SELECT * FROM chipboard WHERE chipboard_id = :chipboardId")
    suspend fun getChipboardById(chipboardId: Int): Chipboard?

    @Query("SELECT * FROM chipboard WHERE union_id = :unionId")
    suspend fun getChipboardsByUnionId(unionId: Int): List<Chipboard>

    @Query("DELETE FROM chipboard WHERE chipboard_id = :chipboardId")
    suspend fun deleteChipboardById(chipboardId: Int)

    @Delete
    suspend fun deleteChipboard(chipboard: Chipboard)

}