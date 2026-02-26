package com.example.halalyticscompose.di

import android.content.Context
import androidx.room.Room
import com.example.halalyticscompose.Data.Local.HalalyticsDatabase
import com.example.halalyticscompose.data.database.ProductHistoryDao
import com.example.halalyticscompose.data.database.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideHalalyticsDatabase(
        @ApplicationContext context: Context
    ): HalalyticsDatabase {
        return Room.databaseBuilder(
            context,
            HalalyticsDatabase::class.java,
            "halalytics_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    fun provideProductHistoryDao(database: HalalyticsDatabase): ProductHistoryDao {
        return database.productHistoryDao()
    }

    @Provides
    fun provideConsumptionDao(database: HalalyticsDatabase): com.example.halalyticscompose.Data.Local.Dao.ConsumptionDao {
        return database.consumptionDao()
    }
}


