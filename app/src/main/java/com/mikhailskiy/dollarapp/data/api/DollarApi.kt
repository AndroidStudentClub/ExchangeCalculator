package com.mikhailskiy.dollarapp.data.api

import com.mikhailskiy.dollarapp.data.api.model.TickerDto
import retrofit2.http.GET
import retrofit2.http.Query

interface DollarApi {

    @GET("v1/tickers")
    suspend fun getTickers(
        @Query("currencies") currencies: String
    ): List<TickerDto>

    @GET("v1/tickers-currencies")
    suspend fun getCurrencies(): List<String>
}