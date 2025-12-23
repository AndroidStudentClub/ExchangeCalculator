package com.mikhailskiy.dollarapp.ui.exchange

import com.mikhailskiy.dollarapp.data.model.Currency

enum class ExchangeType {
    BID, ASK;

    fun toggle(): ExchangeType =
        when (this) {
            BID -> ASK
            ASK -> BID
        }
}

sealed interface ExchangeScreenEvent

sealed interface FormEvent : ExchangeScreenEvent {

    val exchangeType: ExchangeType
        get() = ExchangeType.BID

    data class InitialDataLoaded(
        val fromCurrency: Currency,
        val toCurrency: Currency
    ) : FormEvent

    data class FromCurrencyChanged(
        val currency: Currency
    ) : FormEvent

    data class ToCurrencyChanged(
        val currency: Currency
    ) : FormEvent

    data class FromAmountChanged(
        val amount: String
    ) : FormEvent

    data class ToAmountChanged(
        val amount: String
    ) : FormEvent

    data class Swap(
        val fromCurrency: Currency,
        val toCurrency: Currency,
        val amount: String,
        val toAmount: String
    ) : FormEvent

}