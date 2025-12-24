package com.stoxmon.domain.repository

import com.stoxmon.domain.model.*

interface TickerRepository {
    suspend fun getCandles(ticker: String, timeframe: String): Result<List<Candle>>
    suspend fun getFundamentals(ticker: String): Result<Fundamentals>
    suspend fun getDividends(ticker: String): Result<List<Dividend>>
}

interface NewsRepository {
    suspend fun getNews(ticker: String, rssUrl: String? = null): Result<List<News>>
}

interface PortfolioRepository {
    // Mock implementation for now
    suspend fun getPortfolios(): Result<List<Portfolio>>
    suspend fun getPortfolioTickers(portfolioId: String): Result<List<Ticker>>
}
