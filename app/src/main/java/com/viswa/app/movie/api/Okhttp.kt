package com.viswa.app.movie.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

const val TMDB_API_KEY = "TMDB_API_KEY"
const val query_param_key = "api_key"
val api_key = "139b3efb18532acc15ecb216933c76d0" // getApiKey()

// private fun getApiKey(): String {
//    val path = "/Users/viswanathan.kp/Downloads/Dagger-Hilt-DFM-master/app/key.txt"
//    val properties = Properties()
//    File(path).inputStream().let { properties.load(it) }
//    val temp = properties.getProperty(TMDB_API_KEY, "")
//    println("xxx key is $temp")
//    return temp
// }

private val authQueryAppenderInterceptor: Interceptor = Interceptor { chain ->
    val requestBuilder = chain.request().newBuilder()

    val url = chain.request().url
    val urlBuilder = url.newBuilder()
    if (url.queryParameter(query_param_key) == null) {
        urlBuilder.addQueryParameter(query_param_key, api_key)
    }
    chain.proceed(
        requestBuilder
            .url(urlBuilder.build())
            .build()
    )
}

internal val baseOkHttpClient: OkHttpClient = OkHttpClient
    .Builder()
    .addInterceptor(authQueryAppenderInterceptor)
    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
    .build()
