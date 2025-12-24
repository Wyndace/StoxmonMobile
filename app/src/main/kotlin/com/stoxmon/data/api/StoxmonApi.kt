package com.stoxmon.data.api

import com.stoxmon.data.dto.*
import retrofit2.http.GET
import retrofit2.http.Query

interface StoxmonApi {
    
    @GET("candles")
    suspend fun getCandles(
        @Query("ticker") ticker: String,
        @Query("tf") timeframe: String = "M1"
    ): List<CandleDto>
    
    @GET("fundamentals/pe_pb")
    suspend fun getFundamentals(
        @Query("ticker") ticker: String
    ): FundamentalsDto
    
    @GET("fundamentals/dividends")
    suspend fun getDividends(
        @Query("ticker") ticker: String
    ): DividendsResponseDto
    
    @GET("news")
    suspend fun getNews(
        @Query("ticker") ticker: String,
        @Query("rss_url") rssUrl: String? = null
    ): NewsResponseDto
    
    companion object {
        const val BASE_URL = "http://10.8.1.23:8000/"
    }
}
