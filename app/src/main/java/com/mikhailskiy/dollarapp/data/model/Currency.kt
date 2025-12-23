package com.mikhailskiy.dollarapp.data.model

data class Currency(
    val code: String,
    val symbol: String
) {
    companion object {
        val defaultCurrency = Currency("USDc", "$")
        val Empty = Currency("", "")
    }
}