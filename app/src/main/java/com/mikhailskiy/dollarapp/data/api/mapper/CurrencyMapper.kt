package com.mikhailskiy.dollarapp.data.api.mapper

import androidx.annotation.DrawableRes
import com.mikhailskiy.dollarapp.R
import com.mikhailskiy.dollarapp.data.model.Currency
import com.mikhailskiy.dollarapp.ui.components.CurrencyUi

object CurrencyFlags {

    val map: Map<String, Int> = mapOf(
        "MXN" to R.drawable.ic_flag_mexico,
        "ARS" to R.drawable.ic_flag_argentina,
        "BRL" to R.drawable.ic_flag_brazil,
        "COP" to R.drawable.ic_flag_colombia,
        Currency.defaultCurrency.code to R.drawable.ic_usd
    )
}

@DrawableRes
fun currencyFlagRes(code: String): Int =
    CurrencyFlags.map[code] ?: R.drawable.ic_missing_flag


object CurrencyMapper {

    fun map(currencies: List<Currency>): List<CurrencyUi> {
        return currencies.map { currency ->
            CurrencyUi(code = currency.code, flagRes = currencyFlagRes(currency.code))
        }
    }

}


sealed interface ImageSource {

    // In case of Remote API we can use flagUrl
    data class Remote(
        val url: String
    ) : ImageSource

    data class Local(
        @DrawableRes val resId: Int
    ) : ImageSource
}

data class CurrencyButtonState(
    val label: String,
    val flag: ImageSource,
    val isEnabled: Boolean
)

@DrawableRes
fun getImageByCode(code: String) = currencyFlagRes(code)


fun Currency.toButtonState(): CurrencyButtonState =
    CurrencyButtonState(
        label = code,
        flag = ImageSource.Local(getImageByCode(code)),
        isEnabled = code != Currency.defaultCurrency.code
    )

