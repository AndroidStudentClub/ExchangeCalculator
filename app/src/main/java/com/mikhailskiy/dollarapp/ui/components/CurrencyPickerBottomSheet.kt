package com.mikhailskiy.dollarapp.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikhailskiy.dollarapp.R
import com.mikhailskiy.dollarapp.ui.theme.dimen.CornerRadius.cornerRadius10
import com.mikhailskiy.dollarapp.ui.theme.dimen.Spacing.spacing16
import com.mikhailskiy.dollarapp.ui.theme.dimen.Spacing.spacing8

data class CurrencyUi(
    val code: String,
    @DrawableRes val flagRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyPickerBottomSheet(
    currencies: List<CurrencyUi>,
    selectedCode: String?,
    onSelect: (CurrencyUi) -> Unit,
    onClose: () -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onClose,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {


            BottomSheetHeader(onClose)

            Spacer(Modifier.height(16.dp))

            CurrencyList(
                currencies = currencies,
                selectedCode = selectedCode,
                onSelect = onSelect
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}


@Composable
private fun BottomSheetHeader(onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.dialog_currency_selector_header),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close"
            )
        }
    }
}


@Composable
private fun CurrencyList(
    currencies: List<CurrencyUi>,
    selectedCode: String?,
    onSelect: (CurrencyUi) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 8.dp)
    ) {
        // TODO Make Lazy List
        currencies.forEach { currency ->
            CurrencyItem(
                currency = currency,
                selected = currency.code == selectedCode,
                onClick = { onSelect(currency) }
            )
        }
    }
}


@Composable
private fun CurrencyItem(
    currency: CurrencyUi,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = spacing16, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RoundedCardWithCircleImage(currency.flagRes)

        Spacer(Modifier.width(spacing8))

        Text(
            text = currency.code,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

        if (selected) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(
                        width = 2.dp,
                        color = Color.LightGray,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun RoundedCardWithCircleImage(
    @DrawableRes
    flag: Int
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(cornerRadius10))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(flag),
            contentDescription = "Icon",
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(50))
            ,
            contentScale = ContentScale.Crop
        )
    }
}

@Preview
@Composable
fun CurrencyItemPreview(){
    CurrencyItem(CurrencyUi("USD", R.drawable.ic_usd),true,{})
}

