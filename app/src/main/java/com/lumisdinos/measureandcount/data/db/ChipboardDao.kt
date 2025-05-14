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

    @Query("UPDATE chipboard SET state = :newState WHERE id = :id")
    suspend fun updateChipboardState(id: Int, newState: Int)

    @Query("UPDATE chipboard SET quantity = :newQuantity WHERE id = :id")
    suspend fun updateChipboardQuantity(id: Int, newQuantity: Int)

    @Query("SELECT * FROM chipboard WHERE id = :chipboardId AND union_id = :unionId")
    suspend fun getChipboardByIdAndUnionId(chipboardId: Int, unionId: Int): Chipboard?

    @Query(
        """
        SELECT * FROM chipboard
        WHERE union_id = :unionId
        AND state = 1
        AND id != :chipboardId
        AND color = :color
        AND colorName = :colorName
        AND title1 = :title1
        AND size1 = :size1
        AND realSize1 = :realSize1
        AND title2 = :title2
        AND size2 = :size2
        AND realSize2 = :realSize2
        AND title3 = :title3
        AND size3 = :size3
        AND realSize3 = :realSize3
        LIMIT 1
    """
    )
    suspend fun findSimilarFoundChipboard(
        unionId: Int,
        chipboardId: Int,
        color: Int,
        colorName: String,
        title1: String,
        size1: Float,
        realSize1: Float,
        title2: String,
        size2: Float,
        realSize2: Float,
        title3: String,
        size3: Float,
        realSize3: Float
    ): Chipboard?

    @Query("SELECT * FROM chipboard WHERE union_id = :unionId")
    suspend fun getChipboardsByUnionId(unionId: Int): List<Chipboard>

    @Query("SELECT * FROM chipboard WHERE union_id = :unionId")
    fun getChipboardsFlowByUnionId(unionId: Int): Flow<List<Chipboard>>

    @Query("SELECT COUNT(*) FROM chipboard WHERE union_id = :unionId")
    suspend fun getChipboardsCountByUnionId(unionId: Int): Int

    @Query("SELECT quantity FROM chipboard WHERE id = :id AND union_id = :unionId AND state = :state")
    suspend fun getQuantityOfChipboardByConditions(id: Int, unionId: Int, state: Int): Int?

    @Query("DELETE FROM chipboard WHERE id = :chipboardId")
    suspend fun deleteChipboardById(chipboardId: Int)

    @Delete
    suspend fun deleteChipboard(chipboard: Chipboard)

}