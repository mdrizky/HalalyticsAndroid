package com.example.halalyticscompose.di

import android.util.Log
import com.example.halalyticscompose.BuildConfig
import com.example.halalyticscompose.Data.API.ApiService
import com.example.halalyticscompose.data.api.NotificationApiService
import com.example.halalyticscompose.data.api.OCRProductApiService
import com.example.halalyticscompose.data.api.ProductApiService
import com.example.halalyticscompose.feature.expansion.network.ExpansionApiService
import com.example.halalyticscompose.feature.expansion.socket.ChatWebSocketManager
import com.example.halalyticscompose.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.CertificatePinner
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val DEFAULT_BASE_URL = "http://10.0.2.2:8000/api/"
    private const val TAG = "HalalyticsNetwork"

    @Provides
    @Singleton
    fun provideOkHttpClient(languageInterceptor: LanguageInterceptor): OkHttpClient {
        val requestMetricsInterceptor = Interceptor { chain ->
            val original = chain.request()
            val requestId = UUID.randomUUID().toString().take(8)
            val startNs = System.nanoTime()

            val requestBuilder = original.newBuilder()
                .header("Accept", "application/json")
                .header("X-Request-Id", requestId)

            val currentContentType = original.header("Content-Type")?.lowercase().orEmpty()
            if (original.body != null &&
                currentContentType.isEmpty() &&
                !currentContentType.contains("multipart")
            ) {
                requestBuilder.header("Content-Type", "application/json")
            }

            val request = requestBuilder.method(original.method, original.body).build()

            try {
                val response = chain.proceed(request)
                val tookMs = (System.nanoTime() - startNs) / 1_000_000
                val url = request.url.toString()
                val code = response.code
                val message = response.message

                if (!response.isSuccessful) {
                    val peek = response.peekBody(1024).string().replace("\n", " ").trim()
                    Log.e(TAG, "[$requestId] ${request.method} $url -> $code $message (${tookMs}ms) error=$peek")
                } else {
                    Log.i(TAG, "[$requestId] ${request.method} $url -> $code (${tookMs}ms)")
                }
                response
            } catch (e: Exception) {
                val tookMs = (System.nanoTime() - startNs) / 1_000_000
                Log.e(TAG, "[$requestId] ${request.method} ${request.url} failed (${tookMs}ms): ${e.message}", e)
                throw e
            }
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        val builder = OkHttpClient.Builder()
            .addInterceptor(languageInterceptor)
            .addInterceptor(requestMetricsInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

        // Optional SSL pinning for production HTTPS host.
        val baseUrl = BuildConfig.API_BASE_URL.ifBlank { DEFAULT_BASE_URL }
        val host = baseUrl.toHttpUrlOrNull()?.host
        val pin = BuildConfig.API_CERT_PIN
        if (!host.isNullOrBlank() && pin.isNotBlank() && baseUrl.startsWith("https://")) {
            builder.certificatePinner(
                CertificatePinner.Builder()
                    .add(host, pin)
                    .build()
            )
        }

        return builder.build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val baseUrl = BuildConfig.API_BASE_URL.ifBlank { DEFAULT_BASE_URL }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideProductApiService(retrofit: Retrofit): ProductApiService {
        return retrofit.create(ProductApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideOCRProductApiService(retrofit: Retrofit): OCRProductApiService {
        return retrofit.create(OCRProductApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideNotificationApiService(retrofit: Retrofit): NotificationApiService {
        return retrofit.create(NotificationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideExpansionApiService(retrofit: Retrofit): ExpansionApiService {
        return retrofit.create(ExpansionApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatWebSocketManager(
        okHttpClient: OkHttpClient,
        sessionManager: SessionManager,
    ): ChatWebSocketManager {
        return ChatWebSocketManager(okHttpClient, sessionManager)
    }
}
