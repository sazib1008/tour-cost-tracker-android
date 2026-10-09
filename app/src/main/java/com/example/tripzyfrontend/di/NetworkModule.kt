package com.example.tripzyfrontend.di

import com.example.tripzyfrontend.data.remote.AuthInterceptor
import com.example.tripzyfrontend.data.remote.api.AuthApi
import com.example.tripzyfrontend.data.remote.api.BalanceApi
import com.example.tripzyfrontend.data.remote.api.ExportApi
import com.example.tripzyfrontend.data.remote.api.ExpenseApi
import com.example.tripzyfrontend.data.remote.api.SettlementApi
import com.example.tripzyfrontend.data.remote.api.TourApi
import com.example.tripzyfrontend.data.remote.api.UserApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Uses adb reverse tcp:8080 tcp:8080 for seamless USB / emulator connection
//    private const val BASE_URL = "http://10.0.2.2:8080/"

    private const val BASE_URL = "https://tour-cost-tracker-backend.onrender.com/" // আপনার Port সহ


    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideTourApi(retrofit: Retrofit): TourApi = retrofit.create(TourApi::class.java)

    @Provides
    @Singleton
    fun provideExpenseApi(retrofit: Retrofit): ExpenseApi = retrofit.create(ExpenseApi::class.java)

    @Provides
    @Singleton
    fun provideBalanceApi(retrofit: Retrofit): BalanceApi = retrofit.create(BalanceApi::class.java)

    @Provides
    @Singleton
    fun provideSettlementApi(retrofit: Retrofit): SettlementApi = retrofit.create(SettlementApi::class.java)

    @Provides
    @Singleton
    fun provideExportApi(retrofit: Retrofit): ExportApi = retrofit.create(ExportApi::class.java)
}
