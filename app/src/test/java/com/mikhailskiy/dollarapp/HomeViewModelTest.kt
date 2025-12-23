package com.mikhailskiy.dollarapp

import app.cash.turbine.test
import com.mikhailskiy.dollarapp.data.api.model.TickerDto
import com.mikhailskiy.dollarapp.data.model.Currency
import com.mikhailskiy.dollarapp.domain.CurrencyAndRates
import com.mikhailskiy.dollarapp.domain.GetCurrenciesAndRatesUseCase
import com.mikhailskiy.dollarapp.domain.repository.CurrencyRepository
import com.mikhailskiy.dollarapp.domain.repository.ExchangeRatesRepository
import com.mikhailskiy.dollarapp.ui.exchange.FormEvent
import com.mikhailskiy.dollarapp.ui.exchange.ExchangeViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ExchangeViewModel

    private val usd = Currency("USDc", "$")
    private val eur = Currency("EUR", "€")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val fakeUseCase = object : GetCurrenciesAndRatesUseCase(
            FakeCurrencyRepository(),
            FakeExchangeRatesRepository()
        ) {
            override fun execute() = flow {
                emit(
                    CurrencyAndRates(
                        currencies = listOf(Currency("USDc", "$"), Currency("EUR", "€")),
                        tickers = listOf(TickerDto("1.2", "1.1", "usd_eur", "2025-12-23"))
                    )
                )
            }
        }

        viewModel = ExchangeViewModel(fakeUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `formData updates after Swap event`() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()

        viewModel.formData.test {
            val initial = awaitItem()

            viewModel.handleEvent(
                FormEvent.Swap(
                    fromCurrency = usd,
                    toCurrency = eur,
                    amount = "100",
                    toAmount = "110.0"
                )
            )
            testScheduler.advanceUntilIdle()

            val swapped = awaitItem()
            assertEquals("EUR", swapped.fromCurrency.code)
            assertEquals("USDc", swapped.toCurrency.code)
            assertEquals("100", swapped.toAmount)
        }
    }

    @Test
    fun `showSheet toggles correctly`() {
        assertEquals(false, viewModel.showSheet.value)
        viewModel.openSheet()
        assertEquals(true, viewModel.showSheet.value)
        viewModel.closeSheet()
        assertEquals(false, viewModel.showSheet.value)
    }
}

class FakeCurrencyRepository : CurrencyRepository {
    override suspend fun getCurrencies(): List<String> = listOf("USDc", "EUR")
}

class FakeExchangeRatesRepository : ExchangeRatesRepository {
    override suspend fun getExchangeRates(currencies: String): List<TickerDto> =
        listOf(
            TickerDto("1.2", "1.1", "usd_eur", "2025-12-23")
        )
}
