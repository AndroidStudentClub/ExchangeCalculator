package com.mikhailskiy.dollarapp.data.data_source

class CurrencyLocalDataSource {

    fun getCurrencies(): List<String> =
        listOf(
            "MXN",
            "ARS",
            "BRL",
            "COP"
        )
}