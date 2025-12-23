package com.mikhailskiy.dollarapp.domain.repository

import com.mikhailskiy.dollarapp.data.api.model.TickerDto
import kotlinx.coroutines.flow.Flow

interface ExchangeRatesRepository {
    suspend fun getExchangeRates(currencies: String):  List<TickerDto>
}