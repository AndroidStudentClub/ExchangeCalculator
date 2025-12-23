package com.mikhailskiy.dollarapp

import com.mikhailskiy.dollarapp.data.api.model.TickerDto
import com.mikhailskiy.dollarapp.data.model.Currency
import com.mikhailskiy.dollarapp.ui.util.ExchangeRateCalculator

import org.junit.Test

import org.junit.jupiter.api.Assertions.assertEquals

class ExchangeRateCalculatorTest {

    private val usd = Currency.defaultCurrency
    private val eur = Currency("EUR", "€")
    private val btc = Currency("BTC", "₿")

    @Test
    fun `from default currency uses bid and multiplies`() {
        val rates = listOf(
            TickerDto(
                ask = "0.86",
                bid = "0.85",
                book = "usdc_eur",
                date = "2024-01-01"
            )
        )

        val result = ExchangeRateCalculator.calculateResult(
            fromCurrency = usd,
            toCurrency = eur,
            amount = 100.0,
            exchangeRates = rates
        )

        assertEquals(0.85, result.rate)
        assertEquals(85.00, result.result)
    }

    @Test
    fun `to default currency uses ask and divides`() {
        val rates = listOf(
            TickerDto(
                ask = "0.80",
                bid = "0.85",
                book = "usdc_eur",
                date = "2024-01-01"
            )
        )

        val result = ExchangeRateCalculator.calculateResult(
            fromCurrency = eur,
            toCurrency = usd,
            amount = 80.0,
            exchangeRates = rates
        )

        assertEquals(0.80, result.rate)
        assertEquals(100.00, result.result)
    }

    @Test
    fun `returns zero when ticker not found`() {
        val result = ExchangeRateCalculator.calculateResult(
            fromCurrency = usd,
            toCurrency = btc,
            amount = 50.0,
            exchangeRates = emptyList()
        )

        assertEquals(0.0, result.rate)
        assertEquals(0.0, result.result)
    }

    @Test
    fun `returns zero when ask is zero and dividing`() {
        val rates = listOf(
            TickerDto(
                ask = "0.0",
                bid = "1.0",
                book = "usdc_eur",
                date = "2024-01-01"
            )
        )

        val result = ExchangeRateCalculator.calculateResult(
            fromCurrency = eur,
            toCurrency = usd,
            amount = 100.0,
            exchangeRates = rates
        )

        assertEquals(0.0, result.rate)
        assertEquals(0.0, result.result)
    }

    @Test
    fun `rounds rate to 8 decimals and result to 2 decimals`() {
        val rates = listOf(
            TickerDto(
                ask = "0.0",
                bid = "0.123456789",
                book = "usdc_eur",
                date = "2024-01-01"
            )
        )

        val result = ExchangeRateCalculator.calculateResult(
            fromCurrency = usd,
            toCurrency = eur,
            amount = 10.0,
            exchangeRates = rates
        )

        assertEquals(0.12345679, result.rate)
        assertEquals(1.23, result.result)
    }

    @Test
    fun `invalid numeric values in ticker produce zero result`() {
        val rates = listOf(
            TickerDto(
                ask = "abc",
                bid = "xyz",
                book = "usdc_eur",
                date = "2024-01-01"
            )
        )

        val result = ExchangeRateCalculator.calculateResult(
            fromCurrency = usd,
            toCurrency = eur,
            amount = 100.0,
            exchangeRates = rates
        )

        assertEquals(0.0, result.rate)
        assertEquals(0.0, result.result)
    }

    @Test
    fun `empty currency behaves safely`() {
        val result = ExchangeRateCalculator.calculateResult(
            fromCurrency = Currency.Empty,
            toCurrency = Currency.Empty,
            amount = 100.0,
            exchangeRates = emptyList()
        )

        assertEquals(0.0, result.rate)
        assertEquals(0.0, result.result)
    }
}
