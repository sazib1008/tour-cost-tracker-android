package com.example.tripzyfrontend.data.remote.device

import android.util.Log
import com.example.tripzyfrontend.data.remote.api.UserApi
import com.example.tripzyfrontend.data.remote.dto.RegisterDeviceTokenRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceTokenManager @Inject constructor(
    private val userApi: UserApi
) {
    suspend fun registerDeviceToken(fcmToken: String): Result<Unit> {
        return try {
            val response = userApi.registerDeviceToken(
                RegisterDeviceTokenRequest(token = fcmToken, deviceType = "ANDROID")
            )
            if (response.isSuccessful) {
                Log.d("DeviceTokenManager", "FCM Device Token registered with backend.")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to register token: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeDeviceToken(fcmToken: String): Result<Unit> {
        return try {
            val response = userApi.deleteDeviceToken(fcmToken)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to remove token: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
