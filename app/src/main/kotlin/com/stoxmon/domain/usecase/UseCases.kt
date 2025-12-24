package com.stoxmon.domain.usecase

import com.stoxmon.domain.model.*
import com.stoxmon.domain.repository.*
import javax.inject.Inject

class GetPortfoliosUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    suspend operator fun invoke(): Result<List<Portfolio>> {
        return repository.getPortfolios()
    }
}

class GetPortfolioTickersUseCase @Inject constructor(
    private val repository: PortfolioRepository
) {
    suspend operator fun invoke(portfolioId: String): Result<List<Ticker>> {
        return repository.getPortfolioTickers(portfolioId)
    }
}

class GetTickerCandlesUseCase @Inject constructor(
    private val repository: TickerRepository
) {
    suspend operator fun invoke(ticker: String, timeframe: String = "M1"): Result<List<Candle>> {
        return repository.getCandles(ticker, timeframe)
    }
}

class GetTickerFundamentalsUseCase @Inject constructor(
    private val repository: TickerRepository
) {
    suspend operator fun invoke(ticker: String): Result<Fundamentals> {
        return repository.getFundamentals(ticker)
    }
}

class GetTickerDividendsUseCase @Inject constructor(
    private val repository: TickerRepository
) {
    suspend operator fun invoke(ticker: String): Result<List<Dividend>> {
        return repository.getDividends(ticker)
    }
}

class GetNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(ticker: String, rssUrl: String? = null): Result<List<News>> {
        return repository.getNews(ticker, rssUrl)
    }
}
