package com.example.androidtemplate.features.auth

import com.example.androidtemplate.core.contracts.AndroidAuthContract
import com.example.androidtemplate.core.storage.SessionStore
import com.google.common.truth.Truth.assertThat
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class AuthRepositoryIntegrationTest {
  private lateinit var server: MockWebServer

  @Before
  fun setUp() {
    server = MockWebServer()
    server.start()
  }

  @After
  fun tearDown() {
    server.shutdown()
  }

  @Test
  fun requestOtp_postsToImmutableEndpoint() = runBlocking {
    server.enqueue(MockResponse().setResponseCode(200))

    val repository = AuthRepository(
      baseUrl = server.url("/").toString(),
      redirectUrl = "androidtemplate://auth/callback",
      sessionStore = InMemorySessionStore(),
    )

    val result = repository.requestOtp("user@example.com")

    assertThat(result).isEqualTo(AuthResult.Success)
    val request = server.takeRequest(2, TimeUnit.SECONDS)
    assertThat(request).isNotNull()
    val nonNullRequest = request!!
    assertThat(nonNullRequest.path).isEqualTo(AndroidAuthContract.OTP_ENDPOINT)

    val payload = Json.parseToJsonElement(nonNullRequest.body.readUtf8()).jsonObject
    assertThat(payload["email"]?.toString()).isEqualTo("\"user@example.com\"")
  }

  @Test
  fun verifyOtp_postsToImmutableEndpoint() = runBlocking {
    server.enqueue(
      MockResponse()
        .setResponseCode(200)
        .setBody("{\"access_token\":\"token-abc\"}"),
    )

    val sessionStore = InMemorySessionStore()
    val repository = AuthRepository(
      baseUrl = server.url("/").toString(),
      redirectUrl = "androidtemplate://auth/callback",
      sessionStore = sessionStore,
    )

    val result = repository.verifyOtp(email = "user@example.com", code = "123456")

    assertThat(result).isEqualTo(AuthResult.Success)
    val request = server.takeRequest(2, TimeUnit.SECONDS)
    assertThat(request).isNotNull()
    val nonNullRequest = request!!
    assertThat(nonNullRequest.path).isEqualTo(AndroidAuthContract.VERIFY_ENDPOINT)

    val payload = Json.parseToJsonElement(nonNullRequest.body.readUtf8()).jsonObject
    assertThat(payload["email"]?.toString()).isEqualTo("\"user@example.com\"")
    assertThat(payload["token"]?.toString()).isEqualTo("\"123456\"")
    assertThat(sessionStore.savedAccessToken).isEqualTo("token-abc")
  }

  @Test
  fun logout_postsToImmutableEndpointAndClearsSession() = runBlocking {
    server.enqueue(MockResponse().setResponseCode(200))
    val sessionStore = InMemorySessionStore(token = "access-token")

    val repository = AuthRepository(
      baseUrl = server.url("/").toString(),
      redirectUrl = "androidtemplate://auth/callback",
      sessionStore = sessionStore,
    )

    val result = repository.logout()

    assertThat(result).isEqualTo(AuthResult.Success)
    assertThat(sessionStore.cleared).isTrue()

    val request = server.takeRequest(2, TimeUnit.SECONDS)
    assertThat(request).isNotNull()
    val nonNullRequest = request!!
    assertThat(nonNullRequest.path).isEqualTo(AndroidAuthContract.LOGOUT_ENDPOINT)
    assertThat(nonNullRequest.getHeader("Authorization")).isEqualTo("Bearer access-token")
  }

  @Test
  fun buildOAuthAuthorizeUrl_containsProviderAndRedirect() {
    val repository = AuthRepository(
      baseUrl = "https://example.supabase.co",
      redirectUrl = "androidtemplate://auth/callback",
      sessionStore = InMemorySessionStore(),
    )

    val url = repository.buildOAuthAuthorizeUrl(OAuthProvider.Google)

    assertThat(url).isEqualTo(
      "https://example.supabase.co/auth/v1/authorize" +
        "?provider=google" +
        "&redirect_to=androidtemplate%3A%2F%2Fauth%2Fcallback" +
        "&response_type=token",
    )
  }

  @Test
  fun completeOAuthCallback_extractsAccessTokenAndStoresSession() = runBlocking {
    val sessionStore = InMemorySessionStore()
    val repository = AuthRepository(
      baseUrl = "https://example.supabase.co",
      redirectUrl = "androidtemplate://auth/callback",
      sessionStore = sessionStore,
    )

    val result = repository.completeOAuthCallback(
      "androidtemplate://auth/callback#access_token=oauth-token",
    )

    assertThat(result).isEqualTo(AuthResult.Success)
    assertThat(sessionStore.savedAccessToken).isEqualTo("oauth-token")
  }

  @Test
  fun clearLocalSession_clearsSessionWithoutNetworkCall() = runBlocking {
    val sessionStore = InMemorySessionStore(token = "access-token")
    val repository = AuthRepository(
      baseUrl = "https://example.supabase.co",
      redirectUrl = "androidtemplate://auth/callback",
      sessionStore = sessionStore,
    )

    val result = repository.clearLocalSession()

    assertThat(result).isEqualTo(AuthResult.Success)
    assertThat(sessionStore.cleared).isTrue()
  }

  private class InMemorySessionStore(private var token: String? = null) : SessionStore {
    var cleared: Boolean = false
    var savedAccessToken: String? = null

    override suspend fun accessToken(): String? = token

    override suspend fun saveAccessToken(token: String) {
      savedAccessToken = token
      this.token = token
    }

    override suspend fun clearSession() {
      cleared = true
      token = null
    }
  }
}
