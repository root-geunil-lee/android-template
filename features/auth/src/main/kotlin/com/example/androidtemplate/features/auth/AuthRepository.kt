package com.example.androidtemplate.features.auth

import com.example.androidtemplate.core.contracts.AndroidAuthContract
import com.example.androidtemplate.core.storage.SessionStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class AuthRepository(
  private val baseUrl: String,
  private val sessionStore: SessionStore,
  private val httpClient: OkHttpClient = OkHttpClient(),
  private val json: Json = Json { ignoreUnknownKeys = true },
) : AuthRepositoryContract {

  override suspend fun requestOtp(email: String): AuthResult {
    val payload = json.encodeToString(OtpRequest(email = email))
    return postWithoutAuth(AndroidAuthContract.OTP_ENDPOINT, payload)
  }

  override suspend fun verifyOtp(email: String, code: String): AuthResult {
    val payload = json.encodeToString(OtpVerifyRequest(email = email, token = code, type = "email"))
    return postWithoutAuth(AndroidAuthContract.VERIFY_ENDPOINT, payload)
  }

  override suspend fun logout(): AuthResult {
    val token = sessionStore.accessToken()

    val requestBuilder = Request.Builder()
      .url(baseUrl.trimEnd('/') + AndroidAuthContract.LOGOUT_ENDPOINT)
      .post("{}".toRequestBody("application/json".toMediaType()))

    if (!token.isNullOrBlank()) {
      requestBuilder.header("Authorization", "Bearer $token")
    }

    val response = withContext(Dispatchers.IO) {
      httpClient.newCall(requestBuilder.build()).execute().use { networkResponse ->
        HttpResponse(
          code = networkResponse.code,
          retryAfterSeconds = networkResponse.header("Retry-After")?.toIntOrNull(),
        )
      }
    }

    return if (response.code in 200..299) {
      sessionStore.clearSession()
      AuthResult.Success
    } else if (response.code == 429) {
      AuthResult.RateLimited(response.retryAfterSeconds ?: 30)
    } else {
      AuthResult.Failure("Logout failed (${response.code})")
    }
  }

  private suspend fun postWithoutAuth(path: String, payload: String): AuthResult {
    val request = Request.Builder()
      .url(baseUrl.trimEnd('/') + path)
      .post(payload.toRequestBody("application/json".toMediaType()))
      .build()

    val response = withContext(Dispatchers.IO) {
      httpClient.newCall(request).execute().use { networkResponse ->
        HttpResponse(
          code = networkResponse.code,
          retryAfterSeconds = networkResponse.header("Retry-After")?.toIntOrNull(),
        )
      }
    }

    return if (response.code in 200..299) {
      AuthResult.Success
    } else if (response.code == 429) {
      AuthResult.RateLimited(response.retryAfterSeconds ?: 30)
    } else {
      AuthResult.Failure("Request failed (${response.code})")
    }
  }
}

private data class HttpResponse(
  val code: Int,
  val retryAfterSeconds: Int? = null,
)

@Serializable
private data class OtpRequest(
  val email: String,
)

@Serializable
private data class OtpVerifyRequest(
  val email: String,
  val token: String,
  val type: String,
)
