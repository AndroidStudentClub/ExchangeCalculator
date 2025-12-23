package com.mikhailskiy.dollarapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikhailskiy.dollarapp.R
import com.mikhailskiy.dollarapp.data.api.mapper.CurrencyButtonState
import com.mikhailskiy.dollarapp.data.api.mapper.ImageSource
import com.mikhailskiy.dollarapp.ui.theme.DollarAppTheme
import com.mikhailskiy.dollarapp.ui.util.ExchangeRateFormatter

@Composable
fun CurrencyInputField(
    data: String,
    buttonState: CurrencyButtonState,
    onChange: (value: String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    maxIntegerDigits: Int = ExchangeRateFormatter.MAX_INTEGER_DIGITS
) {
    TextField(
        value = data,
        onValueChange = { input ->
            val cleanInput = input.replace("[^\\d.]".toRegex(), "")

            val parts = cleanInput.split(".")
            var integerPart = parts[0].take(maxIntegerDigits)
            var decimalPart = if (parts.size > 1) parts[1].take(2) else ""

            val newValue = if (decimalPart.isNotEmpty()) {
                "$integerPart.$decimalPart"
            } else {
                integerPart
            }

            onChange(newValue)
        },
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        ),
        placeholder = {
            Text(
                text = "$0.0",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        },
        leadingIcon = {
            CurrencySelectionButton(
                buttonState,
                onClick = onClick,
                modifier = Modifier.wrapContentWidth()
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.tertiary
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        visualTransformation = CurrencyVisualTransformation()
    )
}

class CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val input = text.text
        if (input.isEmpty()) return TransformedText(AnnotatedString(""), OffsetMapping.Identity)

        val parts = input.split(".")
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1) parts[1] else ""

        val reversed = integerPart.reversed()
        val chunks = reversed.chunked(3)
        val formattedInt = chunks.joinToString(",").reversed()

        val formatted = if (decimalPart.isNotEmpty()) "$$formattedInt.$decimalPart" else "$$formattedInt"

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val commasBefore = integerPart.take(offset).length.let { pos ->
                    ((pos - 1) / 3)
                }
                return offset + 1 + commasBefore // +1 для $
            }

            override fun transformedToOriginal(offset: Int): Int {
                val withoutDollar = offset - 1
                val commasBefore = (0 until integerPart.length step 3).count { it < withoutDollar }
                val originalOffset = withoutDollar - commasBefore
                return originalOffset.coerceIn(0, integerPart.length + decimalPart.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

@Composable
fun CurrencySelectionButton(
    buttonState: CurrencyButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Surface(
        modifier = modifier.semantics { Role.Button },
        onClick = onClick,
        enabled = buttonState.isEnabled,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            CurrencyFlag(buttonState)
            Text(
                text = buttonState.label,
                style = MaterialTheme.typography.labelLarge
            )

            if (buttonState.isEnabled) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun CurrencyFlag(
    state: CurrencyButtonState
) {

    val painter: Painter =
        if (state.flag is ImageSource.Local) {
            painterResource(state.flag.resId)
        } else {
            // In case of Remote API we can use flagUrl
            painterResource(R.drawable.ic_missing_flag)
        }


    Image(
        painter = painter,
        contentDescription = "Currency",
        modifier = Modifier
            .size(width = 16.dp, height = 16.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
    )
}

@Preview
@Composable
fun CurrencySelectionButtonPreview() {
    DollarAppTheme {
        CurrencySelectionButton(
            CurrencyButtonState("USDc", ImageSource.Local(resId = R.drawable.ic_usd), true),
            onClick = {}
        )
    }
}

