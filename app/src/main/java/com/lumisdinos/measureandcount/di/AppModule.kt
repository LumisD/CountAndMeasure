package com.lumisdinos.measureandcount.di

import android.content.Context
import androidx.room.Room
import com.lumisdinos.measureandcount.data.MeasureAndCountRepositoryImpl
import com.lumisdinos.measureandcount.data.MeasureAndCountRepository
import com.lumisdinos.measureandcount.data.db.AppDatabase
import com.lumisdinos.measureandcount.data.db.ChipboardDao
import com.lumisdinos.measureandcount.data.db.UnionOfChipboardsDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideUnionOfChipboardsDao(appDatabase: AppDatabase): UnionOfChipboardsDao {
        return appDatabase.unionOfChipboardsDao()
    }

    @Provides
    fun provideChipboardDao(appDatabase: AppDatabase): ChipboardDao {
        return appDatabase.chipboardDao()
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindModule {

    @Binds
    @Singleton
    abstract fun bindMeasureAndCountRepository(
        measureAndCountRepository: MeasureAndCountRepositoryImpl
    ): MeasureAndCountRepository
}