package com.example.halalyticscompose.Data.Local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.halalyticscompose.Data.Local.Dao.CachedScanResultDao
import com.example.halalyticscompose.Data.Local.Dao.ConsumptionDao
import com.example.halalyticscompose.Data.Local.Dao.HaramIngredientDao
import com.example.halalyticscompose.Data.Local.Entities.CachedScanResult
import com.example.halalyticscompose.Data.Local.Entities.Consumption
import com.example.halalyticscompose.Data.Local.Entities.HaramIngredientEntity
import com.example.halalyticscompose.data.database.ProductHistoryDao
import com.example.halalyticscompose.data.database.ProductHistoryEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [
        CachedScanResult::class, 
        Consumption::class, 
        ProductHistoryEntity::class,
        HaramIngredientEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HalalyticsDatabase : RoomDatabase() {
    
    abstract fun cachedScanResultDao(): CachedScanResultDao
    abstract fun consumptionDao(): ConsumptionDao
    abstract fun productHistoryDao(): ProductHistoryDao
    abstract fun haramIngredientDao(): HaramIngredientDao
    
    companion object {
        @Volatile
        private var INSTANCE: HalalyticsDatabase? = null
        
        fun getDatabase(context: Context): HalalyticsDatabase {
            return INSTANCE ?: synchronized(this) {
                val appContext = context.applicationContext
                SQLiteDatabase.loadLibs(appContext)
                val passphrase = SQLiteDatabase.getBytes(
                    "Halalytics_DB_Key_${appContext.packageName}_v1".toCharArray()
                )
                val factory = SupportFactory(passphrase)
                val instance = try {
                    val db = Room.databaseBuilder(
                        appContext,
                        HalalyticsDatabase::class.java,
                        "halalytics_database"
                    )
                        .openHelperFactory(factory)
                        .fallbackToDestructiveMigration()
                        .build()
                    // Force-open to detect mismatch early and recover.
                    db.openHelper.writableDatabase
                    db
                } catch (e: Exception) {
                    // Recovery path for legacy plain/encrypted DB mismatch.
                    appContext.deleteDatabase("halalytics_database")
                    val rebuilt = Room.databaseBuilder(
                        appContext,
                        HalalyticsDatabase::class.java,
                        "halalytics_database"
                    )
                        .openHelperFactory(factory)
                        .fallbackToDestructiveMigration()
                        .build()
                    rebuilt.openHelper.writableDatabase
                    rebuilt
                }
                INSTANCE = instance
                instance
            }
        }
    }
}
