package com.lumisdinos.measureandcount.data.db

import com.lumisdinos.measureandcount.data.db.model.Chipboard
import com.lumisdinos.measureandcount.data.db.model.UnionOfChipboards
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UnionOfChipboards::class,
        Chipboard::class
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun unionOfChipboardsDao(): UnionOfChipboardsDao
    abstract fun chipboardDao(): ChipboardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}