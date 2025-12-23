package com.mikhailskiy.dollarapp.ui.util

import com.mikhailskiy.dollarapp.data.api.model.TickerDto
import com.mikhailskiy.dollarapp.data.model.Currency
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.text.lowercase

data class ExchangeResult(val rate: Double, val result: Double)

object ExchangeRateCalculator {

    fun calculateResult(
        fromCurrency: Currency,
        toCurrency: Currency,
        amount: Double,
        exchangeRates: List<TickerDto>
    ): ExchangeResult {
        val isFromDefault = fromCurrency.code == Currency.defaultCurrency.code


        val book = if (isFromDefault) {
            "${fromCurrency.code.lowercase()}_${toCurrency.code.lowercase()}"
        } else {
            "${toCurrency.code.lowercase()}_${fromCurrency.code.lowercase()}"
        }


        val rate = exchangeRates.firstOrNull { it.book == book }?.let {
            if (isFromDefault) it.bid.toBigDecimalOrNull() else it.ask.toBigDecimalOrNull()
        } ?: BigDecimal.ZERO


        val amountDecimal = BigDecimal.valueOf(amount)
        val result = if (isFromDefault) {
            amountDecimal.multiply(rate)
        } else {
            if (rate.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO
            else amountDecimal.divide(rate, 8, RoundingMode.HALF_UP)
        }

        return ExchangeResult(
            rate = rate.setScale(8, RoundingMode.HALF_UP).toDouble(),
            result = result.setScale(2, RoundingMode.HALF_UP).toDouble()
        )
    }
}