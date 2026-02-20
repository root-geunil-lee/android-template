package com.example.androidtemplate.core.network

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ApiClient(
  private val baseUrl: String,
  private val httpClient: OkHttpClient = OkHttpClient(),
) {
  fun postJson(path: String, bearerToken: String?, payload: String): Int {
    val requestBuilder = Request.Builder()
      .url(baseUrl.trimEnd('/') + path)
      .post(payload.toRequestBody("application/json".toMediaType()))

    if (!bearerToken.isNullOrBlank()) {
      requestBuilder.header("Authorization", "Bearer $bearerToken")
    }

    return httpClient.newCall(requestBuilder.build()).execute().use { it.code }
  }
}
