package com.example.tripzyfrontend.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

sealed interface GoogleSignInResult {
    data class Success(
        val idToken: String,
        val email: String? = null,
        val displayName: String? = null,
        val profilePictureUri: String? = null
    ) : GoogleSignInResult

    object Cancelled : GoogleSignInResult

    data class Failure(
        val error: Throwable,
        val userFacingMessage: String
    ) : GoogleSignInResult
}

@Singleton
class GoogleAuthManager @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    companion object {
        const val WEB_CLIENT_ID = "17160265661-kqeqo57434ck37vsfv9v0ovp0375klui.apps.googleusercontent.com"
        private const val TAG = "GoogleAuthManager"
    }

    /**
     * Initiates Google Sign-In using the Credential Manager API.
     *
     * @param activityContext Context from the calling Activity/Compose hierarchy.
     * @param filterByAuthorizedAccounts If true, only pre-authorized accounts on device are presented.
     * @param autoSelectEnabled If true, automatically signs in if single eligible account is found.
     * @return [GoogleSignInResult] indicating Success with extracted idToken, Cancelled, or Failure.
     */
    suspend fun signIn(
        activityContext: Context,
        filterByAuthorizedAccounts: Boolean = false,
        autoSelectEnabled: Boolean = false
    ): GoogleSignInResult {
        val credentialManager = CredentialManager.create(activityContext)

        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setServerClientId(WEB_CLIENT_ID)
            .setAutoSelectEnabled(autoSelectEnabled)
            .setNonce(hashedNonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val response: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = activityContext
            )
            handleCredentialResponse(response)
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "Google Sign-In cancelled by user.")
            GoogleSignInResult.Cancelled
        } catch (e: GetCredentialInterruptedException) {
            Log.d(TAG, "Google Sign-In interrupted.")
            GoogleSignInResult.Cancelled
        } catch (e: NoCredentialException) {
            Log.w(TAG, "No Google credentials found on device.", e)
            GoogleSignInResult.Failure(
                error = e,
                userFacingMessage = "No Google accounts available on this device. Please add a Google account in system settings."
            )
        } catch (e: GetCredentialCustomException) {
            if (e.type.contains("USER_CANCELED", ignoreCase = true) || e.type.contains("CANCELED", ignoreCase = true)) {
                Log.d(TAG, "User canceled credential prompt: ${e.type}")
                GoogleSignInResult.Cancelled
            } else {
                Log.e(TAG, "Custom credential exception: ${e.type}", e)
                GoogleSignInResult.Failure(
                    error = e,
                    userFacingMessage = e.message ?: "Google sign in failed. Please try again."
                )
            }
        } catch (e: GetCredentialException) {
            Log.e(TAG, "GetCredentialException during Google Sign-In", e)
            GoogleSignInResult.Failure(
                error = e,
                userFacingMessage = e.message ?: "Sign-in failed. Please check your network and Google Play Services."
            )
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception during Google Sign-In", e)
            GoogleSignInResult.Failure(
                error = e,
                userFacingMessage = e.localizedMessage ?: "An unexpected error occurred during Google Sign-In."
            )
        }
    }

    private fun handleCredentialResponse(response: GetCredentialResponse): GoogleSignInResult {
        val credential = response.credential

        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            return try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                if (idToken.isNotBlank()) {
                    Log.d(TAG, "Successfully extracted Google idToken for ${googleIdTokenCredential.id}")
                    GoogleSignInResult.Success(
                        idToken = idToken,
                        email = googleIdTokenCredential.id,
                        displayName = googleIdTokenCredential.displayName,
                        profilePictureUri = googleIdTokenCredential.profilePictureUri?.toString()
                    )
                } else {
                    GoogleSignInResult.Failure(
                        error = IllegalStateException("Extracted Google idToken is empty"),
                        userFacingMessage = "Failed to retrieve authentication token from Google."
                    )
                }
            } catch (e: GoogleIdTokenParsingException) {
                Log.e(TAG, "Failed to parse Google ID token credential", e)
                GoogleSignInResult.Failure(
                    error = e,
                    userFacingMessage = "Failed to process Google authentication response."
                )
            }
        } else {
            Log.w(TAG, "Received unexpected credential type: ${credential::class.java.name}")
            return GoogleSignInResult.Failure(
                error = IllegalArgumentException("Unexpected credential type: ${credential.type}"),
                userFacingMessage = "Unsupported credential received from Google."
            )
        }
    }
}
