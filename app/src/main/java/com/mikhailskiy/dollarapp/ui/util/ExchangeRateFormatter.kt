package com.mikhailskiy.dollarapp.ui.util

import com.mikhailskiy.dollarapp.data.model.Currency
import java.text.NumberFormat
import java.util.Locale

object ExchangeRateFormatter {

    fun format(
        from: String = Currency.defaultCurrency.code,
        to: String,
        rate: Double
    ): String {
        val formattedRate = NumberFormat
            .getNumberInstance(Locale.US)
            .apply {
                minimumFractionDigits = MIN_DIGITS
                maximumFractionDigits = MAX_DIGITS
            }
            .format(rate)

        return "1 $from = $formattedRate $to"
    }

    const val MIN_DIGITS = 4
    const val MAX_DIGITS = 4

    // Max amount like $9,999
    const val MAX_INTEGER_DIGITS = 4
}