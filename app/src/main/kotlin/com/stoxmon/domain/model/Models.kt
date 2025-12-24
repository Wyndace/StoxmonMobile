package com.stoxmon.domain.model

// Portfolio domain model
data class Portfolio(
    val id: String,
    val name: String,
    val type: PortfolioType,
    val totalValue: Double,
    val invested: Double,
    val growthPercent: Double,
    val growthAmount: Double,
    val payoutPercent: Double? = null,
    val payoutAmount: Double? = null
)

enum class PortfolioType {
    REAL,
    FAKE
}

// Ticker domain model
data class Ticker(
    val symbol: String,
    val companyName: String,
    val logoUrl: String? = null,
    val currentPrice: Double,
    val priceUnit: String = "₽",
    val quantity: Int? = null,
    val shares: Double? = null,
    val yieldPercent: Double? = null,
    val yieldAmount: Double? = null
)

// News domain model
data class News(
    val id: String,
    val ticker: String,
    val companyName: String,
    val title: String,
    val content: String,
    val publishedDate: String,
    val sourceIcon: String? = null
)

// Candle domain model for charts
data class Candle(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)

// Fundamentals
data class Fundamentals(
    val pe: Double?,
    val pb: Double?,
    val dividendYield: Double?,
    val marketCap: Double?
)

data class Dividend(
    val date: String,
    val amount: Double,
    val currency: String
)
