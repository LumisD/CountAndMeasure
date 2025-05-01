package com.lumisdinos.measureandcount.data

import com.lumisdinos.measureandcount.data.db.ChipboardDao
import com.lumisdinos.measureandcount.data.db.UnionOfChipboardsDao
import com.lumisdinos.measureandcount.data.db.model.toChipboardUi
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.lumisdinos.measureandcount.ui.model.ChipboardUi
import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI
import com.lumisdinos.measureandcount.ui.model.toChipboard
import com.lumisdinos.measureandcount.ui.model.toUnionOfChipboards

class MeasureAndCountRepositoryImpl @Inject constructor(
    private val unionOfChipboardsDao: UnionOfChipboardsDao,
    private val chipboardDao: ChipboardDao
) : MeasureAndCountRepository {

    override suspend fun insertChipboard(chipboardUi: ChipboardUi) {
        val entity = chipboardUi.toChipboard()
        chipboardDao.insertChipboard(entity)
    }

    override suspend fun insertUnionOfChipboards(unionOfChipboardsUI: UnionOfChipboardsUI): Int {
        val entity = unionOfChipboardsUI.toUnionOfChipboards()
        return unionOfChipboardsDao.insertUnionOfChipboards(entity).toInt()
    }

    override suspend fun updateUnionOfChipboardsTitle(unionId: Int, newTitle: String, updatedAt: Long) {
        unionOfChipboardsDao.updateUnionOfChipboardsTitle(unionId, newTitle,  updatedAt)
    }

    override fun getChipboardsByUnionIdFlow(unionId: Int): Flow<List<ChipboardUi>> {
        return chipboardDao.getChipboardsFlowByUnionId(unionId).map { chipboardEntities ->
            chipboardEntities.map { it.toChipboardUi() }
        }
    }

    override suspend fun deleteChipboardById(chipboardId: Int) {
        chipboardDao.deleteChipboardById(chipboardId)
    }

    override suspend fun getChipboardsCountByUnionId(unionId: Int): Int {
        return chipboardDao.getChipboardsCountByUnionId(unionId)
    }

    override suspend fun deleteUnionOfChipboards(unionId: Int) {
        unionOfChipboardsDao.deleteUnionOfChipboardsById(unionId)
    }



}