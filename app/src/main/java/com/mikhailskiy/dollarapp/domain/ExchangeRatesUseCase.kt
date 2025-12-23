package com.mikhailskiy.dollarapp.domain

import com.mikhailskiy.dollarapp.data.api.model.TickerDto
import com.mikhailskiy.dollarapp.data.model.Currency
import com.mikhailskiy.dollarapp.domain.repository.CurrencyRepository
import com.mikhailskiy.dollarapp.domain.repository.ExchangeRatesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

data class CurrencyAndRates(
    val currencies: List<Currency>,
    val tickers: List<TickerDto>
)

open class GetCurrenciesAndRatesUseCase @Inject constructor(
    private val repository: CurrencyRepository,
    private val exchangeRatesRepository: ExchangeRatesRepository
) {
    open fun execute(): Flow<CurrencyAndRates> = flow {
        val currencies = repository.getCurrencies()

        val currenciesParam = currencies.joinToString(",")

        val exchangeRates: List<TickerDto> = try {
            exchangeRatesRepository.getExchangeRates(currenciesParam)
        } catch (e: Exception) {
            emptyList()
        }

        emit(CurrencyAndRates(currencies.map { Currency(code = it, symbol = it) }, exchangeRates))
    }.flowOn(Dispatchers.IO)
}
