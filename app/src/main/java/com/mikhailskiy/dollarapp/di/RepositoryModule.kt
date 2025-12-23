package com.mikhailskiy.dollarapp.di

import com.mikhailskiy.dollarapp.data.repository.CurrencyRepositoryImpl
import com.mikhailskiy.dollarapp.data.repository.ExchangeRatesRemoteRepository
import com.mikhailskiy.dollarapp.domain.repository.CurrencyRepository
import com.mikhailskiy.dollarapp.domain.repository.ExchangeRatesRepository
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
    abstract fun bindCurrencyRepository(
        impl: CurrencyRepositoryImpl
    ): CurrencyRepository


    @Binds
    @Singleton
    abstract fun bindExchangeRatesRemoteRepository(
        impl: ExchangeRatesRemoteRepository
    ): ExchangeRatesRepository

}