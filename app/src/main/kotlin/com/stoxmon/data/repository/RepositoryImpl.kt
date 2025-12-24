package com.stoxmon.data.repository

import com.stoxmon.data.dto.*
import com.stoxmon.domain.model.*
import com.stoxmon.domain.repository.NewsRepository
import com.stoxmon.domain.repository.PortfolioRepository
import com.stoxmon.domain.repository.TickerRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TickerRepositoryImpl @Inject constructor(
    private val api: StoxmonApi
) : TickerRepository {
    
    // In-memory cache
    private val candlesCache = mutableMapOf<String, Pair<Long, List<Candle>>>()
    private val fundamentalsCache = mutableMapOf<String, Pair<Long, Fundamentals>>()
    private val dividendsCache = mutableMapOf<String, Pair<Long, List<Dividend>>>()
    
    private val cacheTimeout = 5 * 60 * 1000L // 5 minutes
    
    override suspend fun getCandles(ticker: String, timeframe: String): Result<List<Candle>> {
        return try {
            val cacheKey = "$ticker-$timeframe"
            val cached = candlesCache[cacheKey]
            
            if (cached != null && System.currentTimeMillis() - cached.first < cacheTimeout) {
                return Result.success(cached.second)
            }
            
            val response = api.getCandles(ticker, timeframe)
            val candles = response.map { it.toDomain() }
            
            candlesCache[cacheKey] = Pair(System.currentTimeMillis(), candles)
            Result.success(candles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFundamentals(ticker: String): Result<Fundamentals> {
        return try {
            val cached = fundamentalsCache[ticker]
            
            if (cached != null && System.currentTimeMillis() - cached.first < cacheTimeout) {
                return Result.success(cached.second)
            }
            
            val response = api.getFundamentals(ticker)
            val fundamentals = response.toDomain()
            
            fundamentalsCache[ticker] = Pair(System.currentTimeMillis(), fundamentals)
            Result.success(fundamentals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getDividends(ticker: String): Result<List<Dividend>> {
        return try {
            val cached = dividendsCache[ticker]
            
            if (cached != null && System.currentTimeMillis() - cached.first < cacheTimeout) {
                return Result.success(cached.second)
            }
            
            val response = api.getDividends(ticker)
            val dividends = response.dividends.map { it.toDomain() }
            
            dividendsCache[ticker] = Pair(System.currentTimeMillis(), dividends)
            Result.success(dividends)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val api: StoxmonApi
) : NewsRepository {
    
    private val newsCache = mutableMapOf<String, Pair<Long, List<News>>>()
    private val cacheTimeout = 10 * 60 * 1000L // 10 minutes
    
    override suspend fun getNews(ticker: String, rssUrl: String?): Result<List<News>> {
        return try {
            val cacheKey = "$ticker-${rssUrl ?: "default"}"
            val cached = newsCache[cacheKey]
            
            if (cached != null && System.currentTimeMillis() - cached.first < cacheTimeout) {
                return Result.success(cached.second)
            }
            
            val response = api.getNews(ticker, rssUrl)
            val companyName = getCompanyName(ticker)
            val news = response.news.map { it.toDomain(ticker, companyName) }
            
            newsCache[cacheKey] = Pair(System.currentTimeMillis(), news)
            Result.success(news)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun getCompanyName(ticker: String): String {
        return when (ticker.uppercase()) {
            "ROSN" -> "Роснефть"
            "UGLD" -> "ЮГК"
            "NVTK" -> "Новатэк"
            "SBER" -> "Сбер"
            else -> ticker
        }
    }
}

@Singleton
class PortfolioRepositoryImpl @Inject constructor() : PortfolioRepository {
    
    // Mock data based on design
    override suspend fun getPortfolios(): Result<List<Portfolio>> {
        delay(300) // Simulate network delay
        
        val portfolios = listOf(
            Portfolio(
                id = "1",
                name = "Дивидендный",
                type = PortfolioType.REAL,
                totalValue = 86520.50,
                invested = 83594.0,
                growthPercent = -21.5,
                growthAmount = -17972.0,
                payoutPercent = 25.0,
                payoutAmount = 20898.50
            ),
            Portfolio(
                id = "2",
                name = "Спекулятивный",
                type = PortfolioType.FAKE,
                totalValue = 447906.80,
                invested = 203594.0,
                growthPercent = 120.0,
                growthAmount = 244312.80,
                payoutPercent = null,
                payoutAmount = null
            )
        )
        
        return Result.success(portfolios)
    }
    
    override suspend fun getPortfolioTickers(portfolioId: String): Result<List<Ticker>> {
        delay(300)
        
        val tickers = listOf(
            Ticker(
                symbol = "ROSN",
                companyName = "Роснефть",
                currentPrice = 407.0,
                quantity = 5,
                shares = null,
                yieldPercent = 2.83,
                yieldAmount = 11.56
            ),
            Ticker(
                symbol = "UGLD",
                companyName = "ЮГК",
                currentPrice = 476.0,
                quantity = 1000,
                shares = 5.0,
                yieldPercent = null,
                yieldAmount = null
            ),
            Ticker(
                symbol = "NVTK",
                companyName = "Новатэк",
                currentPrice = 1055.0,
                quantity = null,
                shares = 3.5,
                yieldPercent = null,
                yieldAmount = null
            ),
            Ticker(
                symbol = "SBER",
                companyName = "Сбер",
                currentPrice = 150.0,
                quantity = null,
                shares = 3.0,
                yieldPercent = null,
                yieldAmount = null
            )
        )
        
        return Result.success(tickers)
    }
}
