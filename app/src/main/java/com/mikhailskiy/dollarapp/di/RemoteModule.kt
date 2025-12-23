package com.mikhailskiy.dollarapp.di

import com.mikhailskiy.dollarapp.data.api.DollarApi
import com.mikhailskiy.dollarapp.data.data_source.CurrencyRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun provideRemoteDataSource(api: DollarApi): CurrencyRemoteDataSource =
        CurrencyRemoteDataSource(api)
}