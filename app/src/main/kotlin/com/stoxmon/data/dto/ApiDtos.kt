package com.stoxmon.data.dto

import com.google.gson.annotations.SerializedName

// Candles API response
data class CandleDto(
    @SerializedName("time")
    val time: String,  // Дата приходит как строка: "2025-12-08 06:59:59"

    @SerializedName("open")
    val open: Double,

    @SerializedName("high")
    val high: Double,

    @SerializedName("low")
    val low: Double,

    @SerializedName("close")
    val close: Double,

    @SerializedName("volume")
    val volume: Long
)

// PE/PB API response
data class FundamentalsDto(
    @SerializedName("Ticker")
    val ticker: String,

    @SerializedName("P/E")
    val peRaw: String?,

    @SerializedName("P/B")
    val pbRaw: String?
)

// Dividends API response
data class DividendDto(
    @SerializedName("date")
    val date: String,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("currency")
    val currency: String
)

data class DividendsResponseDto(
    @SerializedName("ticker")
    val ticker: String,

    @SerializedName("dividends")
    val dividends: List<DividendDto>
)

// News API response
data class NewsItemDto(
    @SerializedName("title")
    val title: String,

    @SerializedName("link")
    val link: String,

    @SerializedName("published")
    val published: String,

    @SerializedName("summary")
    val summary: String?
)

data class NewsResponseDto(
    @SerializedName("ticker")
    val ticker: String,

    @SerializedName("news")
    val news: List<NewsItemDto>
)