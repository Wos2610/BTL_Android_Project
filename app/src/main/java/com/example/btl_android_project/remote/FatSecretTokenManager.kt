package com.example.btl_android_project.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.example.btl_android_project.remote.domain.FatSecretAuthRemoteDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class FatSecretTokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRemoteDataSource: FatSecretAuthRemoteDataSource
) {
    private val mutex = Mutex()

    suspend fun getAccessToken(): String {
        return withContext(Dispatchers.IO) {
            mutex.withLock {
                // Retrieve current token from Secure DataStore
                val currentToken = context.secureTokenDataStore.data.first()

                // Check if token exists and is not expired
                if (isTokenValid(currentToken)) {
                    return@withContext currentToken.accessToken
                }

                // Refresh token
                refreshAccessToken()
            }
        }
    }

    private fun isTokenValid(token: TokenResponse): Boolean {
        return token.accessToken.isNotBlank() &&
                System.currentTimeMillis() < token.expiresAt
    }

    private suspend fun refreshAccessToken(): String {
        val result = authRemoteDataSource.getAccessToken()
        println("Result: $result")
        return when (result) {
            is Resource.Success -> {
                val tokenData = result.data
                tokenData?.let {
                    // Log token data
                    println("Token Data: $it")
                    val newToken = TokenResponse(
                        accessToken = tokenData.accessToken,
                        expiresIn = tokenData.expiresIn
                    )

                    // Save new token to Secure DataStore
                    context.secureTokenDataStore.updateData { newToken }

                    newToken.accessToken
                }
            }

            is Resource.DataError<*>,
            is Resource.Exception<*> -> {
                throw IOException("Failed to refresh access token")
            }
        }.toString()
    }
}

// Token Response Data Class
@Serializable
data class TokenResponse(
    val accessToken: String,
    val expiresIn: Int
) {
    val expiresAt: Long = System.currentTimeMillis() +
            (expiresIn - 60) * 1000 // Subtract 60 seconds as buffer
}

object TokenSerializer : Serializer<TokenResponse> {
    override val defaultValue: TokenResponse
        get() = TokenResponse("", 0)

    override suspend fun readFrom(input: InputStream): TokenResponse {
        return try {
            val jsonString = input.bufferedReader().use { it.readText() }
            Json.decodeFromString(jsonString)
        } catch (e: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: TokenResponse, output: OutputStream) {
        output.write(Json.encodeToString(t).toByteArray())
    }
}

val Context.secureTokenDataStore: DataStore<TokenResponse> by dataStore(
    fileName = "secure_token.pb",
    serializer = TokenSerializer
)
