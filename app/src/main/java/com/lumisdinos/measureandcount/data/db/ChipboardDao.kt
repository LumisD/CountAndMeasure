package com.lumisdinos.measureandcount.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lumisdinos.measureandcount.data.db.model.Chipboard
import kotlinx.coroutines.flow.Flow

@Dao
interface ChipboardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChipboard(chipboard: Chipboard)

    @Query("SELECT * FROM chipboard WHERE id = :chipboardId")
    suspend fun getChipboardById(chipboardId: Int): Chipboard?

    @Query("SELECT * FROM chipboard WHERE union_id = :unionId")
    suspend fun getChipboardsByUnionId(unionId: Int): List<Chipboard>

    @Query("SELECT * FROM chipboard WHERE union_id = :unionId")
    fun getChipboardsFlowByUnionId(unionId: Int): Flow<List<Chipboard>>

    @Query("SELECT COUNT(*) FROM chipboard WHERE union_id = :unionId")
    suspend fun getChipboardsCountByUnionId(unionId: Int): Int

    @Query("SELECT quantity FROM chipboard WHERE id = :id AND union_id = :unionId AND state = :state")
    suspend fun getQuantityOfChipboardByConditions(id: Int, unionId: Int, state : Int): Short?

    @Query("UPDATE chipboard SET state = :newState WHERE id = :id")
    suspend fun updateChipboardState(id: Int, newState: Int)

    @Query("DELETE FROM chipboard WHERE id = :chipboardId")
    suspend fun deleteChipboardById(chipboardId: Int)

    @Delete
    suspend fun deleteChipboard(chipboard: Chipboard)

}