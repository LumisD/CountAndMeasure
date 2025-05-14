package com.lumisdinos.measureandcount.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lumisdinos.measureandcount.data.db.model.UnionOfChipboards
import kotlinx.coroutines.flow.Flow

@Dao
interface UnionOfChipboardsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnionOfChipboards(unionOfChipboards: UnionOfChipboards): Long

    @Query("UPDATE union_of_chipboards SET title = :newTitle, updated_at = :updatedAt WHERE id = :unionId")
    suspend fun updateUnionOfChipboardsTitle(unionId: Int, newTitle: String, updatedAt: Long)

    @Query(
        """
    UPDATE union_of_chipboards
    SET
        dimensions = :dimensions,
        direction = :direction,
        has_color = :hasColor,
        title_column1 = :titleColumn1,
        title_column2 = :titleColumn2,
        title_column3 = :titleColumn3
    WHERE id = :unionId
"""
    )
    suspend fun updateUnionCharacteristics(
        unionId: Int,
        dimensions: Int,
        direction: Int,
        hasColor: Boolean,
        titleColumn1: String,
        titleColumn2: String,
        titleColumn3: String
    )

    @Query("UPDATE union_of_chipboards SET is_finished = :isFinished, updated_at = :updatedAt WHERE id = :unionId")
    suspend fun setUnionOfChipboardsIsFinished(unionId: Int, isFinished: Boolean, updatedAt: Long)

    @Query("SELECT * FROM union_of_chipboards WHERE id = :unionId")
    suspend fun getUnionOfChipboardsById(unionId: Int): UnionOfChipboards?

    @Query("SELECT * FROM union_of_chipboards")
    fun getAllUnionsFlow(): Flow<List<UnionOfChipboards>>

    @Query(
        """
    SELECT * FROM union_of_chipboards
    WHERE is_finished = 0
    ORDER BY 
        CASE 
            WHEN updated_at > created_at THEN updated_at
            ELSE created_at
        END DESC
    LIMIT 1
    """
    )
    suspend fun getLastUnFinishedUnionOfChipboards(): UnionOfChipboards?

    @Query("DELETE FROM union_of_chipboards WHERE id = :unionId")
    suspend fun deleteUnionOfChipboardsById(unionId: Int)

}