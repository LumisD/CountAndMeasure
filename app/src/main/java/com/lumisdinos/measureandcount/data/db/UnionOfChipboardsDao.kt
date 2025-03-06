package com.lumisdinos.measureandcount.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lumisdinos.measureandcount.data.db.model.UnionOfChipboards

@Dao
interface UnionOfChipboardsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnionOfChipboards(unionOfChipboards: UnionOfChipboards)

    @Query("SELECT * FROM union_of_chipboards WHERE union_id = :unionId")
    suspend fun getUnionOfChipboardsById(unionId: Int): UnionOfChipboards?

    @Query("DELETE FROM union_of_chipboards WHERE union_id = :unionId")
    suspend fun deleteUnionOfChipboardsById(unionId: Int)

}