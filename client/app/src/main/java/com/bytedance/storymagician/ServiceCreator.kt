package com.bytedance.storymagician

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit
import androidx.core.content.edit

object ServiceCreator {
    private const val BASE_URL = "http://14.103.19.244:8000"
    private const val PREFS_FILE = "story_magician_prefs"
    private const val PREF_DEVICE_ID = "device_uuid"

    private lateinit var deviceUuid: String
    private lateinit var retrofit: Retrofit

    /**
     * Initializes the ServiceCreator with application context.
     * This method should be called once in the Application's onCreate.
     * It sets up the persistent device UUID and configures the HTTP client.
     */
    fun init(context: Context) {
        // Get or create the device UUID from SharedPreferences
        val prefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        var uuid = prefs.getString(PREF_DEVICE_ID, null)
        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
            prefs.edit { putString(PREF_DEVICE_ID, uuid) }
        }
        deviceUuid = uuid
        Log.d("ServiceCreator", "Device UUID: $deviceUuid")
        // Build the OkHttpClient with an interceptor to add the UUID header
        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-Device-UUID", deviceUuid)
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        // Build the Retrofit instance
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(serviceClass: Class<T>): T {
        if (!::retrofit.isInitialized) {
            throw IllegalStateException("ServiceCreator must be initialized in Application class before use.")
        }
        return retrofit.create(serviceClass)
    }

    inline fun <reified T> create(): T = create(T::class.java)
}
