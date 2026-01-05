package com.example.halalyticscompose.Data.Network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    
    // ⚠️ IMPORTANT: Change based on your setup
    // For Android Emulator: use 10.0.2.2
    // For Real Device: use your computer's IP address (e.g., 192.168.1.100)
    // For Production: use your domain (e.g., https://api.halalytics.com/api/)
    
    private const val BASE_URL = "http://10.0.2.2:8000/api/"
    
    // Alternative URLs (uncomment as needed):
    // private const val BASE_URL = "http://192.168.1.100:8000/api/" // For real device
    // private const val BASE_URL = "https://your-domain.com/api/" // For production
    
    /**
     * Create OkHttpClient with logging interceptor
     */
    private fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    
    /**
     * Create Retrofit instance
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Get External API Service (for OpenFoodFacts products)
     */
    fun getExternalApiService(): ExternalApiService {
        return retrofit.create(ExternalApiService::class.java)
    }
    
    /**
     * Create authenticated client with Bearer token
     */
    fun getAuthenticatedApiService(token: String): ExternalApiService {
        val authInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("Accept", "application/json")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
        
        val client = provideOkHttpClient().newBuilder()
            .addInterceptor(authInterceptor)
            .build()
        
        val authenticatedRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        return authenticatedRetrofit.create(ExternalApiService::class.java)
    }
}
