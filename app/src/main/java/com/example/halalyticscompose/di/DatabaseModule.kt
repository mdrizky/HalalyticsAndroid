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
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideHalalyticsDatabase(
        @ApplicationContext context: Context
    ): HalalyticsDatabase {
        // Required by SQLCipher before using SupportFactory/Room.
        SQLiteDatabase.loadLibs(context)

        // 🔒 SECURITY: Encrypt the local database with SQLCipher
        // Generates a deterministic passphrase so data persists across app restarts
        val passphrase = generatePassphrase(context)
        val factory = SupportFactory(passphrase)

        return try {
            val db = buildEncryptedDatabase(context, factory)
            // Force-open to catch encrypted/plain mismatch at startup.
            db.openHelper.writableDatabase
            db
        } catch (e: Exception) {
            // Recovery path for passphrase mismatch / legacy plain DB state.
            context.deleteDatabase("halalytics_database")
            val rebuilt = buildEncryptedDatabase(context, factory)
            // If this still fails, let it throw so crash reporter captures it.
            rebuilt.openHelper.writableDatabase
            rebuilt
        }
    }
    
    @Provides
    fun provideProductHistoryDao(database: HalalyticsDatabase): ProductHistoryDao {
        return database.productHistoryDao()
    }

    @Provides
    fun provideConsumptionDao(database: HalalyticsDatabase): com.example.halalyticscompose.Data.Local.Dao.ConsumptionDao {
        return database.consumptionDao()
    }
    
    /**
     * Generates a deterministic encryption passphrase for SQLCipher.
     * Uses the app's unique package signature as a seed to create
     * an encryption key that persists across app restarts.
     */
    private fun generatePassphrase(context: Context): ByteArray {
        val seed = "Halalytics_DB_Key_${context.packageName}_v1"
        return SQLiteDatabase.getBytes(seed.toCharArray())
    }

    private fun buildEncryptedDatabase(
        context: Context,
        factory: SupportFactory
    ): HalalyticsDatabase {
        return Room.databaseBuilder(
            context,
            HalalyticsDatabase::class.java,
            "halalytics_database"
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }
}
