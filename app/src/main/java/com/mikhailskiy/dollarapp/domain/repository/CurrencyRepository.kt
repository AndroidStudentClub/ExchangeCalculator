package com.mikhailskiy.dollarapp.domain.repository

interface CurrencyRepository {
    suspend fun getCurrencies(): List<String>
}
