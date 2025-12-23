package com.mikhailskiy.dollarapp.ui.exchange

import androidx.compose.runtime.Immutable
import com.mikhailskiy.dollarapp.data.api.model.TickerDto
import com.mikhailskiy.dollarapp.data.model.Currency

data class FormData(
    val fromCurrency: Currency = Currency.Empty,
    val toCurrency: Currency = Currency.Empty,
    val fromAmount: String = "",
    val toAmount: String = "",
    val exchangeType: ExchangeType = ExchangeType.BID,
    val exchangeRate: Double = 0.0
)

@Immutable
sealed class ExchangeScreenUiState {
    data class Error(val message: String) : ExchangeScreenUiState()

    @Immutable
    data object Loading : ExchangeScreenUiState()

    @Immutable
    data class Success(
        val currencies: List<Currency>,
        val exchangeRates: List<TickerDto>,
        val formData: FormData
    ) : ExchangeScreenUiState()
}
