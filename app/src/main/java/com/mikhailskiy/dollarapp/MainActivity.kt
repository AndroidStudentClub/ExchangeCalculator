package com.mikhailskiy.dollarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikhailskiy.dollarapp.data.api.mapper.CurrencyMapper
import com.mikhailskiy.dollarapp.data.api.mapper.toButtonState
import com.mikhailskiy.dollarapp.data.model.Currency
import com.mikhailskiy.dollarapp.ui.components.CurrencyInputField
import com.mikhailskiy.dollarapp.ui.components.CurrencyPickerBottomSheet
import com.mikhailskiy.dollarapp.ui.exchange.FormEvent
import com.mikhailskiy.dollarapp.ui.exchange.ExchangeScreenEvent
import com.mikhailskiy.dollarapp.ui.exchange.ExchangeScreenUiState
import com.mikhailskiy.dollarapp.ui.exchange.ExchangeViewModel
import com.mikhailskiy.dollarapp.ui.theme.DollarAppTheme
import com.mikhailskiy.dollarapp.ui.theme.dimen.Spacing
import com.mikhailskiy.dollarapp.ui.util.ExchangeRateFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val viewModel: ExchangeViewModel by viewModels()
            DollarAppTheme {
                Scaffold { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ExchangeScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeScreen(
    viewModel: ExchangeViewModel,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is ExchangeScreenUiState.Success -> ExchangeScreenContent(
            state = state,
            onEvent = viewModel::handleEvent,
            viewModel = viewModel
        )

        ExchangeScreenUiState.Loading -> {
            LoadingScreen()
        }

        is ExchangeScreenUiState.Error -> TODO()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeScreenContent(
    viewModel: ExchangeViewModel,
    state: ExchangeScreenUiState.Success,
    onEvent: (ExchangeScreenEvent) -> Unit,
) {
    val formData = state.formData

    var selected by remember(state.currencies) {
        mutableStateOf(state.currencies.firstOrNull()?.code)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.spacing16)
    ) {

        Text(
            text = stringResource(R.string.header),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .wrapContentWidth()
                .padding(top = Spacing.spacing24, bottom = Spacing.spacing8)
        )

        Text(
            text = ExchangeRateFormatter.format(
                to = selected ?: "",
                rate = formData.exchangeRate
            ),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            modifier = Modifier
                .wrapContentWidth()
                .padding(bottom = Spacing.spacing24)
        )


        val showSheet by viewModel.showSheet.collectAsState()
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            snapshotFlow { showSheet }
                .distinctUntilChanged()
                .collect { visible ->
                    if (visible) sheetState.show()
                    else sheetState.hide()
                }
        }

        var showFromCurrencyPicker by remember { mutableStateOf(false) }
        var showToCurrencyPicker by remember { mutableStateOf(false) }

        val currenciesUI = remember(state.currencies) {
            CurrencyMapper.map(state.currencies)
        }

        if (sheetState.isVisible) {
            CurrencyPickerBottomSheet(
                currencies = currenciesUI,
                selectedCode = selected,
                sheetState = sheetState,
                onSelect = {
                    if (showFromCurrencyPicker) {
                        onEvent(
                            FormEvent.FromCurrencyChanged(
                                Currency(
                                    code = it.code,
                                    symbol = it.code
                                )
                            )
                        )
                    } else {
                        onEvent(
                            FormEvent.ToCurrencyChanged(
                                Currency(
                                    code = it.code,
                                    symbol = it.code
                                )
                            )
                        )
                    }
                    selected = it.code
                    scope.launch {
                        showFromCurrencyPicker = false
                        showToCurrencyPicker = false
                        sheetState.hide()
                        viewModel.closeSheet()
                    }
                },
                onClose = {
                    scope.launch {
                        showFromCurrencyPicker = false
                        showToCurrencyPicker = false
                        sheetState.hide()
                        viewModel.closeSheet()
                    }
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                CurrencyInputField(
                    data = formData.fromAmount,
                    buttonState = formData.fromCurrency.toButtonState(),
                    onChange = { onEvent(FormEvent.FromAmountChanged(it)) },
                    onClick = {
                        showFromCurrencyPicker = true
                        viewModel.openSheet()
                    }
                )

                Spacer(modifier = Modifier.height(Spacing.spacing16))

                CurrencyInputField(
                    data = formData.toAmount,
                    formData.toCurrency.toButtonState(),
                    onChange = { onEvent(FormEvent.ToAmountChanged(it)) },
                    onClick = {
                        showToCurrencyPicker = true
                        viewModel.openSheet()
                    }
                )
            }

            val swapClick = remember(formData) {
                {
                    onEvent(
                        FormEvent.Swap(
                            fromCurrency = formData.fromCurrency,
                            toCurrency = formData.toCurrency,
                            amount = formData.fromAmount,
                            toAmount = formData.toAmount
                        )
                    )
                }
            }

            SwapButton(onClick = swapClick, modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun SwapButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    outerColor: Color = MaterialTheme.colorScheme.background,
    innerColor: Color = MaterialTheme.colorScheme.primary,
    iconColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(36.dp),
        shape = CircleShape,
        color = outerColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(Spacing.spacing24)
                    .clip(CircleShape)
                    .background(innerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_swap),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(Spacing.spacing16)
                )
            }
        }
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.loading)
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
