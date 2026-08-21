package com.example.tripzyfrontend.di

import com.example.tripzyfrontend.data.repository.AuthRepositoryImpl
import com.example.tripzyfrontend.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: com.example.tripzyfrontend.data.repository.AuthRepositoryImpl
    ): com.example.tripzyfrontend.domain.repository.AuthRepository

    @Binds
    @Singleton
    abstract fun bindTourRepository(
        tourRepositoryImpl: com.example.tripzyfrontend.data.repository.TourRepositoryImpl
    ): com.example.tripzyfrontend.domain.repository.TourRepository

    @Binds
    @Singleton
    abstract fun bindBalanceRepository(
        balanceRepositoryImpl: com.example.tripzyfrontend.data.repository.BalanceRepositoryImpl
    ): com.example.tripzyfrontend.domain.repository.BalanceRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        expenseRepositoryImpl: com.example.tripzyfrontend.data.repository.ExpenseRepositoryImpl
    ): com.example.tripzyfrontend.domain.repository.ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindSettlementRepository(
        settlementRepositoryImpl: com.example.tripzyfrontend.data.repository.SettlementRepositoryImpl
    ): com.example.tripzyfrontend.domain.repository.SettlementRepository

    @Binds
    @Singleton
    abstract fun bindExportRepository(
        exportRepositoryImpl: com.example.tripzyfrontend.data.repository.ExportRepositoryImpl
    ): com.example.tripzyfrontend.domain.repository.ExportRepository
}

