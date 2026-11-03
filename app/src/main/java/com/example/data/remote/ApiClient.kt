package com.arnafoundation.app.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

// ============================================================
// ARNA GRAM UNNAYA FOUNDATION
// API CLIENT
// ============================================================

object ApiClient {

    // ========================================================
    // MOSHI
    // ========================================================

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()


    // ========================================================
    // AUTH / API INTERCEPTOR
    // ========================================================

    private val authInterceptor = Interceptor { chain ->

        val original = chain.request()

        val requestBuilder = original.newBuilder()
            .header(
                "Accept",
                "application/json"
            )
            .header(
                "X-API-Key",
                ApiConfigManager.currentConfig.apiKey
            )

        /*
         * Only set the default JSON Content-Type when
         * the request does not already define a body type.
         *
         * This preserves requests such as:
         * application/x-www-form-urlencoded
         */
        if (
            original.body?.contentType() == null &&
            original.header("Content-Type") == null
        ) {
            requestBuilder.header(
                "Content-Type",
                "application/json"
            )
        }

        // ====================================================
        // BEARER SESSION TOKEN
        // ====================================================

        val sessionToken =
            ApiConfigManager.currentSessionToken

        if (!sessionToken.isNullOrBlank()) {

            requestBuilder.header(
                "Authorization",
                "Bearer $sessionToken"
            )
        }

        val request = requestBuilder.build()

        chain.proceed(request)
    }


    // ========================================================
    // HTTP LOGGING
    // ========================================================
    //
    // BASIC logging is intentionally used.
    //
    // This avoids logging request/response bodies where
    // passwords, PINs, authentication data or other secrets
    // could appear.
    //
    // ========================================================

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {

            level = HttpLoggingInterceptor.Level.BASIC
        }


    // ========================================================
    // OKHTTP CLIENT
    // ========================================================

    private val okHttpClient: OkHttpClient by lazy {

        OkHttpClient.Builder()

            .addInterceptor(authInterceptor)

            .addInterceptor(loggingInterceptor)

            .connectTimeout(
                30,
                TimeUnit.SECONDS
            )

            .readTimeout(
                30,
                TimeUnit.SECONDS
            )

            .writeTimeout(
                30,
                TimeUnit.SECONDS
            )

            .build()
    }


    // ========================================================
    // RETROFIT INSTANCE
    // ========================================================

    private var retrofitInstance: Retrofit? = null

    private var lastBaseUrl: String? = null


    // ========================================================
    // RAW OKHTTP CLIENT
    // ========================================================

    fun getRawOkHttpClient(): OkHttpClient {
        return okHttpClient
    }


    // ========================================================
    // API SERVICE
    // ========================================================

    fun getApiService(): ApiService {

        val currentBaseUrl =
            ApiConfigManager.currentConfig.baseUrl

        /*
         * Re-create Retrofit when the configured base URL
         * changes.
         */
        if (
            retrofitInstance == null ||
            lastBaseUrl != currentBaseUrl
        ) {

            lastBaseUrl = currentBaseUrl

            retrofitInstance = Retrofit.Builder()

                .baseUrl(
                    if (currentBaseUrl.endsWith("/")) {
                        currentBaseUrl
                    } else {
                        "$currentBaseUrl/"
                    }
                )

                .client(okHttpClient)

                .addConverterFactory(
                    MoshiConverterFactory.create(moshi)
                )

                .build()
        }

        return retrofitInstance!!
            .create(ApiService::class.java)
    }
}