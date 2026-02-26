package com.example.halalyticscompose.di

import com.example.halalyticscompose.utils.PreferenceManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class LanguageInterceptor @Inject constructor(
    private val preferenceManager: PreferenceManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val languageCode = runBlocking {
            preferenceManager.appLanguage.first()
        }

        val request = chain.request().newBuilder()
            .addHeader("Accept-Language", languageCode)
            .build()
        
        return chain.proceed(request)
    }
}
