package com.mikhailskiy.dollarapp.di

import com.mikhailskiy.dollarapp.domain.GetCurrenciesAndRatesUseCase
import com.mikhailskiy.dollarapp.domain.repository.CurrencyRepository
import com.mikhailskiy.dollarapp.domain.repository.ExchangeRatesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetCurrenciesAndRatesUseCase(
        repository: CurrencyRepository,
        exchangeRatesRepository: ExchangeRatesRepository
    ): GetCurrenciesAndRatesUseCase =
        GetCurrenciesAndRatesUseCase(repository, exchangeRatesRepository)
}