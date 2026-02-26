package com.example.halalyticscompose.Data.Local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.halalyticscompose.Data.Local.Dao.CachedScanResultDao
import com.example.halalyticscompose.Data.Local.Dao.ConsumptionDao
import com.example.halalyticscompose.Data.Local.Entities.CachedScanResult
import com.example.halalyticscompose.Data.Local.Entities.Consumption
import com.example.halalyticscompose.data.database.ProductHistoryDao
import com.example.halalyticscompose.data.database.ProductHistoryEntity

@Database(
    entities = [CachedScanResult::class, Consumption::class, ProductHistoryEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HalalyticsDatabase : RoomDatabase() {
    
    abstract fun cachedScanResultDao(): CachedScanResultDao
    abstract fun consumptionDao(): ConsumptionDao
    abstract fun productHistoryDao(): ProductHistoryDao
    
    companion object {
        @Volatile
        private var INSTANCE: HalalyticsDatabase? = null
        
        fun getDatabase(context: Context): HalalyticsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HalalyticsDatabase::class.java,
                    "halalytics_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
