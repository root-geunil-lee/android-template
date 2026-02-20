package com.example.androidtemplate.features.auth

import com.example.androidtemplate.core.contracts.AndroidAuthContract
import com.example.androidtemplate.core.storage.SessionStore
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class AuthRepository(
  private val baseUrl: String,
  private val redirectUrl: String,
  private val sessionStore: SessionStore,
  private val httpClient: OkHttpClient = OkHttpClient(),
  private val json: Json = Json { ignoreUnknownKeys = true },
) : AuthRepositoryContract {

  override fun buildOAuthAuthorizeUrl(provider: OAuthProvider): String? {
    if (baseUrl.isBlank() || redirectUrl.isBlank()) {
      return null
    }

    val encodedRedirect = URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8.name())
    return baseUrl.trimEnd('/') +
      "/auth/v1/authorize?provider=${provider.providerKey}" +
      "&redirect_to=$encodedRedirect" +
      "&response_type=token"
  }

  override suspend fun completeOAuthCallback(callbackUri: String): AuthResult {
    val params = parseCallbackParameters(callbackUri)
    val errorMessage = params["error_description"] ?: params["error"]
    if (!errorMessage.isNullOrBlank()) {
      return AuthResult.Failure(errorMessage)
    }

    val accessToken = params["access_token"]
    if (!accessToken.isNullOrBlank()) {
      sessionStore.saveAccessToken(accessToken)
      return AuthResult.Success
    }

    // Supabase may return authorization code depending on flow settings.
    val authorizationCode = params["code"]
    if (!authorizationCode.isNullOrBlank()) {
      return AuthResult.Success
    }

    return AuthResult.Failure("Invalid OAuth callback")
  }

  override suspend fun requestOtp(email: String): AuthResult {
    val payload = json.encodeToString(OtpRequest(email = email))
    return postWithoutAuth(AndroidAuthContract.OTP_ENDPOINT, payload)
  }

  override suspend fun verifyOtp(email: String, code: String): AuthResult {
    val payload = json.encodeToString(OtpVerifyRequest(email = email, token = code, type = "email"))
    val response = executePostWithoutAuth(AndroidAuthContract.VERIFY_ENDPOINT, payload)

    return if (response.code in 200..299) {
      response.body
        ?.let(::extractAccessToken)
        ?.takeIf { it.isNotBlank() }
        ?.let { token -> sessionStore.saveAccessToken(token) }
      AuthResult.Success
    } else if (response.code == 429) {
      AuthResult.RateLimited(response.retryAfterSeconds ?: 30)
    } else {
      AuthResult.Failure("Request failed (${response.code})")
    }
  }

  override suspend fun logout(): AuthResult {
    val token = sessionStore.accessToken()

    val requestBuilder = Request.Builder()
      .url(baseUrl.trimEnd('/') + AndroidAuthContract.LOGOUT_ENDPOINT)
      .post("{}".toRequestBody("application/json".toMediaType()))

    if (!token.isNullOrBlank()) {
      requestBuilder.header("Authorization", "Bearer $token")
    }

    val response = runCatching {
      withContext(Dispatchers.IO) {
        httpClient.newCall(requestBuilder.build()).execute().use { networkResponse ->
          HttpResponse(
            code = networkResponse.code,
            retryAfterSeconds = networkResponse.header("Retry-After")?.toIntOrNull(),
          )
        }
      }
    }.getOrElse {
      return AuthResult.Failure("Network error")
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
    val response = executePostWithoutAuth(path, payload)

    return if (response.code in 200..299) {
      AuthResult.Success
    } else if (response.code == 429) {
      AuthResult.RateLimited(response.retryAfterSeconds ?: 30)
    } else {
      AuthResult.Failure("Request failed (${response.code})")
    }
  }

  private suspend fun executePostWithoutAuth(path: String, payload: String): HttpResponse {
    val request = Request.Builder()
      .url(baseUrl.trimEnd('/') + path)
      .post(payload.toRequestBody("application/json".toMediaType()))
      .build()

    val response = runCatching {
      withContext(Dispatchers.IO) {
        httpClient.newCall(request).execute().use { networkResponse ->
          HttpResponse(
            code = networkResponse.code,
            body = networkResponse.body?.string(),
            retryAfterSeconds = networkResponse.header("Retry-After")?.toIntOrNull(),
          )
        }
      }
    }.getOrElse {
      return HttpResponse(code = -1)
    }
    return response
  }
}

private data class HttpResponse(
  val code: Int,
  val body: String? = null,
  val retryAfterSeconds: Int? = null,
)

private fun extractAccessToken(body: String): String? {
  return runCatching {
    Json.parseToJsonElement(body)
      .jsonObject["access_token"]
      ?.jsonPrimitive
      ?.contentOrNull
  }.getOrNull()
}

private fun parseCallbackParameters(callbackUri: String): Map<String, String> {
  val uri = runCatching { URI(callbackUri) }.getOrNull() ?: return emptyMap()
  val queryPairs = parseKeyValuePairs(uri.rawQuery)
  val fragmentPairs = parseKeyValuePairs(uri.rawFragment)
  return queryPairs + fragmentPairs
}

private fun parseKeyValuePairs(rawValue: String?): Map<String, String> {
  if (rawValue.isNullOrBlank()) {
    return emptyMap()
  }

  return rawValue.split("&")
    .mapNotNull { token ->
      val separator = token.indexOf('=')
      if (separator <= 0) {
        return@mapNotNull null
      }

      val key = decodeUrlPart(token.substring(0, separator))
      val value = decodeUrlPart(token.substring(separator + 1))
      key to value
    }
    .toMap()
}

private fun decodeUrlPart(rawPart: String): String {
  return URLDecoder.decode(rawPart, StandardCharsets.UTF_8.name())
}

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
