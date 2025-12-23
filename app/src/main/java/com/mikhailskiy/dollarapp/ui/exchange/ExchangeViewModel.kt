package com.mikhailskiy.dollarapp.ui.exchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikhailskiy.dollarapp.data.model.Currency
import com.mikhailskiy.dollarapp.domain.CurrencyAndRates
import com.mikhailskiy.dollarapp.domain.GetCurrenciesAndRatesUseCase
import com.mikhailskiy.dollarapp.ui.util.ExchangeRateCalculator.calculateResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.floor


@HiltViewModel
class ExchangeViewModel @Inject constructor(
    repository: GetCurrenciesAndRatesUseCase
) : ViewModel() {

    private val _formEventsFlow: MutableSharedFlow<FormEvent> = MutableSharedFlow()
    private val _formDataFlow: MutableStateFlow<FormData> = MutableStateFlow(FormData())
    val formData: StateFlow<FormData> = _formDataFlow.asStateFlow()

    private val currencyAndRatesFlow = repository.execute()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CurrencyAndRates(emptyList(), emptyList())
        )

    val uiState: StateFlow<ExchangeScreenUiState> = combine(
        _formDataFlow,
        currencyAndRatesFlow
    ) { formData, currencyAndRates ->
        ExchangeScreenUiState.Success(
            currencies = currencyAndRates.currencies,
            exchangeRates = currencyAndRates.tickers,
            formData
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ExchangeScreenUiState.Loading
    )

    init {
        viewModelScope.launch {
            currencyAndRatesFlow
                .filter { it.currencies.isNotEmpty() }
                .take(1)
                .collect { currencyAndRates ->
                    val defaultFrom = Currency.defaultCurrency
                    val defaultTo = currencyAndRates.currencies.first()
                    _formEventsFlow.emit(
                        FormEvent.InitialDataLoaded(
                            fromCurrency = defaultFrom,
                            toCurrency = defaultTo
                        )
                    )

                    val result = calculateResult(
                        fromCurrency = defaultFrom,
                        toCurrency = defaultTo,
                        amount = 0.0,
                        exchangeRates = currencyAndRates.tickers,
                    )
                    val calculatedResult = result.result.roundToTwoDecimals()
                    _formDataFlow.update {
                        it.copy(
                            toAmount = calculatedResult.toString(),
                            exchangeRate = result.rate
                        )
                    }
                }
        }
    }

    init {
        viewModelScope.launch {
            _formEventsFlow
                .filter { event ->
                    when (event) {
                        is FormEvent.FromAmountChanged -> {
                            if (event.amount.isBlank()) {
                                _formDataFlow.update { it.copy(fromAmount = "", toAmount = "") }
                            }
                            event.amount.isNotBlank()
                        }

                        is FormEvent.ToAmountChanged -> {
                            if (event.amount.isBlank()) {
                                _formDataFlow.update { it.copy(toAmount = "", fromAmount = "") }
                            }
                            event.amount.isNotBlank()
                        }

                        else -> true
                    }
                }
                .filter { event ->
                    when (event) {
                        is FormEvent.FromAmountChanged -> event.amount.toDoubleOrNull() != null
                        is FormEvent.ToAmountChanged -> event.amount.toDoubleOrNull() != null
                        is FormEvent.InitialDataLoaded -> event.fromCurrency != Currency.Empty && event.toCurrency != Currency.Empty
                        else -> true
                    }
                }
                .onEach { event ->
                    _formDataFlow.update { data ->
                        when (event) {
                            is FormEvent.InitialDataLoaded -> data.copy(
                                fromCurrency = event.fromCurrency,
                                toCurrency = event.toCurrency
                            )

                            is FormEvent.FromAmountChanged -> data.copy(
                                fromAmount = event.amount,
                                exchangeType = ExchangeType.BID
                            )

                            is FormEvent.FromCurrencyChanged -> data.copy(
                                fromCurrency = event.currency,
                                exchangeType = ExchangeType.BID
                            )

                            is FormEvent.ToAmountChanged -> data.copy(toAmount = event.amount)
                            is FormEvent.ToCurrencyChanged -> data.copy(toCurrency = event.currency)
                            is FormEvent.Swap -> data.copy(
                                fromCurrency = event.toCurrency, toCurrency = event.fromCurrency,
                                toAmount = event.amount, fromAmount = event.toAmount,
                                exchangeType = event.exchangeType.toggle()
                            )

                        }
                    }
                }
                .collectLatest { event ->
                    if (event is FormEvent.InitialDataLoaded) return@collectLatest

                    val exchangeRates = when (val state = uiState.value) {
                        is ExchangeScreenUiState.Success -> {
                            state.exchangeRates
                        }

                        else -> return@collectLatest
                    }
                    val data = _formDataFlow.value

                    when (event) {


                        is FormEvent.Swap -> {
                            val result = calculateResult(
                                fromCurrency = data.fromCurrency,
                                toCurrency = data.toCurrency,
                                amount = data.fromAmount.toDoubleOrNull() ?: 0.0,
                                exchangeRates = exchangeRates
                            )

                            val calculatedResult = result.result.roundToTwoDecimals()
                            _formDataFlow.update {
                                it.copy(
                                    fromAmount = data.fromAmount,
                                    toAmount = calculatedResult.toString(),
                                    exchangeRate = result.rate
                                )
                            }
                        }

                        is FormEvent.FromAmountChanged,
                        is FormEvent.FromCurrencyChanged,

                        is FormEvent.ToCurrencyChanged -> {
                            val result = calculateResult(
                                fromCurrency = data.fromCurrency,
                                toCurrency = data.toCurrency,
                                amount = data.fromAmount.toDoubleOrNull() ?: 0.0,
                                exchangeRates = exchangeRates,
                            )
                            val calculatedResult = result.result.roundToTwoDecimals()
                            _formDataFlow.update {
                                it.copy(
                                    toAmount = calculatedResult.toString(),
                                    exchangeRate = result.rate
                                )
                            }
                        }

                        is FormEvent.ToAmountChanged -> {
                            val result = calculateResult(
                                fromCurrency = data.toCurrency,
                                toCurrency = data.fromCurrency,
                                amount = data.toAmount.toDoubleOrNull() ?: 0.0,
                                exchangeRates = exchangeRates
                            )
                            val calculatedResult = result.result.roundToTwoDecimals()
                            _formDataFlow.update {
                                it.copy(
                                    fromAmount = calculatedResult.toString(),
                                    exchangeRate = result.rate
                                )
                            }
                        }

                        else -> Unit
                    }
                }
        }
    }

    fun handleEvent(event: ExchangeScreenEvent) {
        viewModelScope.launch {
            when (event) {
                is FormEvent -> _formEventsFlow.emit(event)
            }
        }
    }

    private val _showSheet = MutableStateFlow(false)
    val showSheet: StateFlow<Boolean> = _showSheet

    fun openSheet() {
        _showSheet.value = true
    }

    fun closeSheet() {
        _showSheet.value = false
    }

    private fun Double.roundToTwoDecimals(): Double {
        return floor(this * 100) / 100
    }

}