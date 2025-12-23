package com.mikhailskiy.dollarapp.data.repository

import com.mikhailskiy.dollarapp.data.api.DollarApi
import com.mikhailskiy.dollarapp.data.api.model.TickerDto
import com.mikhailskiy.dollarapp.domain.repository.ExchangeRatesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRatesRemoteRepository @Inject constructor(
    private val api: DollarApi
) : ExchangeRatesRepository {

    override suspend fun getExchangeRates(currencies: String): List<TickerDto> {
        return api.getTickers(currencies)
    }
}