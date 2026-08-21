package com.example.tripzyfrontend.data.remote.api

import com.example.tripzyfrontend.data.remote.dto.AddExpenseCommentRequest
import com.example.tripzyfrontend.data.remote.dto.AddMemberRequest
import com.example.tripzyfrontend.data.remote.dto.ApiResponse
import com.example.tripzyfrontend.data.remote.dto.CreateExpenseRequest
import com.example.tripzyfrontend.data.remote.dto.CreateTourRequest
import com.example.tripzyfrontend.data.remote.dto.ExpenseCommentDto
import com.example.tripzyfrontend.data.remote.dto.ExpenseDto
import com.example.tripzyfrontend.data.remote.dto.JoinTourRequest
import com.example.tripzyfrontend.data.remote.dto.RecordSettlementRequest
import com.example.tripzyfrontend.data.remote.dto.SettlementDto
import com.example.tripzyfrontend.data.remote.dto.TourBalanceResponseDto
import com.example.tripzyfrontend.data.remote.dto.TourDetailDto
import com.example.tripzyfrontend.data.remote.dto.TourSummaryDto
import com.example.tripzyfrontend.data.remote.dto.UpdateExpenseRequest
import com.example.tripzyfrontend.data.remote.dto.UpdateTourRequest
import com.example.tripzyfrontend.data.remote.dto.UpdateTourStatusRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface TourApi {
    @GET("tours")
    suspend fun getMyTours(): Response<ApiResponse<List<TourSummaryDto>>>

    @POST("tours")
    suspend fun createTour(@Body request: CreateTourRequest): Response<ApiResponse<TourDetailDto>>

    @GET("tours/{id}")
    suspend fun getTourDetails(@Path("id") tourId: String): Response<ApiResponse<TourDetailDto>>

    @PUT("tours/{id}")
    suspend fun updateTour(@Path("id") tourId: String, @Body request: UpdateTourRequest): Response<ApiResponse<TourDetailDto>>

    @POST("tours/join")
    suspend fun joinTourByCode(@Body request: JoinTourRequest): Response<ApiResponse<TourDetailDto>>

    @POST("tours/{id}/members")
    suspend fun addMember(@Path("id") tourId: String, @Body request: AddMemberRequest): Response<ApiResponse<TourDetailDto>>

    @DELETE("tours/{id}/members/{targetUserId}")
    suspend fun removeMember(@Path("id") tourId: String, @Path("targetUserId") targetUserId: String): Response<ApiResponse<Unit>>

    @PATCH("tours/{id}/status")
    suspend fun updateTourStatus(@Path("id") tourId: String, @Body request: UpdateTourStatusRequest): Response<ApiResponse<TourDetailDto>>
}

interface ExpenseApi {
    @POST("tours/{tourId}/expenses")
    suspend fun createExpense(
        @Path("tourId") tourId: String,
        @Header("Idempotency-Key") idempotencyKey: String?,
        @Body request: CreateExpenseRequest
    ): Response<ApiResponse<ExpenseDto>>

    @GET("tours/{tourId}/expenses")
    suspend fun getTourExpenses(@Path("tourId") tourId: String): Response<ApiResponse<List<ExpenseDto>>>

    @GET("tours/{tourId}/expenses/{expenseId}")
    suspend fun getExpenseDetails(
        @Path("tourId") tourId: String,
        @Path("expenseId") expenseId: String
    ): Response<ApiResponse<ExpenseDto>>

    @PUT("tours/{tourId}/expenses/{expenseId}")
    suspend fun updateExpense(
        @Path("tourId") tourId: String,
        @Path("expenseId") expenseId: String,
        @Body request: UpdateExpenseRequest
    ): Response<ApiResponse<ExpenseDto>>

    @DELETE("tours/{tourId}/expenses/{expenseId}")
    suspend fun deleteExpense(
        @Path("tourId") tourId: String,
        @Path("expenseId") expenseId: String
    ): Response<ApiResponse<Unit>>

    @GET("tours/{tourId}/expenses/{expenseId}/comments")
    suspend fun getExpenseComments(
        @Path("tourId") tourId: String,
        @Path("expenseId") expenseId: String
    ): Response<ApiResponse<List<ExpenseCommentDto>>>

    @POST("tours/{tourId}/expenses/{expenseId}/comments")
    suspend fun addExpenseComment(
        @Path("tourId") tourId: String,
        @Path("expenseId") expenseId: String,
        @Body request: AddExpenseCommentRequest
    ): Response<ApiResponse<ExpenseCommentDto>>
}

interface BalanceApi {
    @GET("tours/{tourId}/balance")
    suspend fun getTourBalance(@Path("tourId") tourId: String): Response<ApiResponse<TourBalanceResponseDto>>
}

interface SettlementApi {
    @POST("tours/{tourId}/settlements")
    suspend fun recordSettlement(
        @Path("tourId") tourId: String,
        @Body request: RecordSettlementRequest
    ): Response<ApiResponse<SettlementDto>>

    @GET("tours/{tourId}/settlements")
    suspend fun getTourSettlements(@Path("tourId") tourId: String): Response<ApiResponse<List<SettlementDto>>>
}

interface ExportApi {
    @Streaming
    @GET("tours/{tourId}/export")
    suspend fun exportTour(
        @Path("tourId") tourId: String,
        @Query("format") format: String = "pdf"
    ): Response<ResponseBody>
}
