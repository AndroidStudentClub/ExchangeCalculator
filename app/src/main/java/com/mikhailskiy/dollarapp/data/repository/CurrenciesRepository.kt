package com.mikhailskiy.dollarapp.data.repository

import com.mikhailskiy.dollarapp.data.data_source.CurrencyLocalDataSource
import com.mikhailskiy.dollarapp.data.data_source.CurrencyRemoteDataSource
import com.mikhailskiy.dollarapp.domain.repository.CurrencyRepository
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val remote: CurrencyRemoteDataSource,
    private val local: CurrencyLocalDataSource
) : CurrencyRepository {

    override suspend fun getCurrencies(): List<String> {
        return try {
            remote.getCurrencies()
        } catch (e: Exception) {
            // TODO Log error
            local.getCurrencies()
        }
    }
}