package com.example.halalyticscompose.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [ProductHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class HalalyticsDatabase : RoomDatabase() {
    
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
