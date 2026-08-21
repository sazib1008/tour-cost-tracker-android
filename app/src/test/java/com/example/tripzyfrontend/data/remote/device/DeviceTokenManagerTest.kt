package com.example.tripzyfrontend.data.remote.device

import com.example.tripzyfrontend.data.remote.api.UserApi
import com.example.tripzyfrontend.data.remote.dto.ApiResponse
import com.example.tripzyfrontend.data.remote.dto.RegisterDeviceTokenRequest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class DeviceTokenManagerTest {

    private val userApi: UserApi = mockk()
    private lateinit var deviceTokenManager: DeviceTokenManager

    @Before
    fun setUp() {
        deviceTokenManager = DeviceTokenManager(userApi)
    }

    @Test
    fun `registerDeviceToken returns success when API responds with 200 OK`() = runTest {
        val request = RegisterDeviceTokenRequest(token = "fcm-token-123", deviceType = "ANDROID")
        coEvery { userApi.registerDeviceToken(request) } returns Response.success(ApiResponse(true, "Token registered", null))

        val result = deviceTokenManager.registerDeviceToken("fcm-token-123")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `removeDeviceToken returns success when API responds with 200 OK`() = runTest {
        coEvery { userApi.deleteDeviceToken("fcm-token-123") } returns Response.success(ApiResponse(true, "Token removed", null))

        val result = deviceTokenManager.removeDeviceToken("fcm-token-123")

        assertTrue(result.isSuccess)
    }
}
