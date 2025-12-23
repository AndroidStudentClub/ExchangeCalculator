package com.mikhailskiy.dollarapp.di

import com.mikhailskiy.dollarapp.data.data_source.CurrencyLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideLocalDataSource(): CurrencyLocalDataSource =
        CurrencyLocalDataSource()
}