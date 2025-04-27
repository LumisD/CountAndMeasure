//package com.lumisdinos.measureandcount.ui.screens.addnewitem
//
//import com.lumisdinos.measureandcount.data.MeasureAndCountRepository
//import com.lumisdinos.measureandcount.data.db.ChipboardDao
//import com.lumisdinos.measureandcount.data.db.UnionOfChipboardsDao
//import com.lumisdinos.measureandcount.data.db.model.Chipboard
//import com.lumisdinos.measureandcount.data.db.model.UnionOfChipboards
//import kotlinx.coroutines.flow.Flow
//
//class FakeMeasureAndCountRepository : MeasureAndCountRepository {
//    // Implement the repository methods with dummy data for preview
//}
//
//class FakeUnionOfChipboardsDao : UnionOfChipboardsDao {
//    override suspend fun insert(unionOfChipboards: UnionOfChipboards) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun delete(unionOfChipboards: UnionOfChipboards) {
//        TODO("Not yet implemented")
//    }
//
//    override fun getAll(): Flow<List<UnionOfChipboards>> {
//        TODO("Not yet implemented")
//    }
//}
//
//class FakeChipboardDao : ChipboardDao {
//    override suspend fun insert(chipboard: Chipboard) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun delete(chipboard: Chipboard) {
//        TODO("Not yet implemented")
//    }
//
//    override fun getAll(): Flow<List<Chipboard>> {
//        TODO("Not yet implemented")
//    }
//}
