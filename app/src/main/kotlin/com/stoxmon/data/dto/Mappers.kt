package com.stoxmon.data.dto

import com.stoxmon.domain.model.*
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

private fun String.toDoubleOrNullComma(): Double? = replace(",", ".").toDoubleOrNull();

// Mapper extensions
fun CandleDto.toDomain(): Candle {
    // Парсим дату формата "2025-12-08 06:59:59" в timestamp
    val timestamp = try {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        format.parse(time)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        // Если не удалось распарсить, используем текущее время
        System.currentTimeMillis()
    }

    return Candle(
        timestamp = timestamp,
        open = open,
        high = high,
        low = low,
        close = close,
        volume = volume
    )
}

fun FundamentalsDto.toDomain(): Fundamentals {
    return Fundamentals(
        pe = peRaw?.toDoubleOrNullComma(),
        pb = pbRaw?.toDoubleOrNullComma(),
        dividendYield = null,
        marketCap = null
    )
}

fun DividendDto.toDomain(): Dividend {
    return Dividend(
        date = date,
        amount = amount,
        currency = currency
    )
}

fun NewsItemDto.toDomain(ticker: String, companyName: String): News {
    return News(
        id = UUID.randomUUID().toString(),
        ticker = ticker,
        companyName = companyName,
        title = title,
        content = summary ?: "",
        publishedDate = published,
        sourceIcon = null
    )
}