package com.mikhailskiy.dollarapp.data.data_source

import com.mikhailskiy.dollarapp.data.api.DollarApi
import javax.inject.Inject

class CurrencyRemoteDataSource @Inject constructor(
    private val api: DollarApi
) {

    suspend fun getCurrencies(): List<String> =
        api.getCurrencies()
}
