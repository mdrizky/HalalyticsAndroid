package com.example.halalyticscompose.di

import android.content.Context
import com.example.halalyticscompose.data.notification.NotificationService
import com.example.halalyticscompose.utils.SessionManager
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
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }
    
    @Provides
    @Singleton
    fun provideNotificationService(@ApplicationContext context: Context): NotificationService {
        return NotificationService(context)
    }

    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context: Context): com.example.halalyticscompose.utils.PreferenceManager {
        return com.example.halalyticscompose.utils.PreferenceManager(context)
    }
}
