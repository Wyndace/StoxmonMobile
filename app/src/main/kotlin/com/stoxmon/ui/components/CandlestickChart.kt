package com.stoxmon.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stoxmon.domain.model.Candle
import com.stoxmon.ui.theme.*
import kotlin.math.max
import kotlin.math.min

@Composable
fun CandlestickChart(
    candles: List<Candle>,
    modifier: Modifier = Modifier,
    showVolume: Boolean = false
) {
    if (candles.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            CardGradientStart.copy(alpha = 0.3f),
                            CardGradientEnd.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Нет данных для отображения",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }

    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundCard.copy(alpha = 0.5f),
                        BackgroundDark.copy(alpha = 0.8f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        // Chart info
        val lastCandle = candles.lastOrNull()
        if (lastCandle != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "O: ${lastCandle.open.format(2)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "C: ${lastCandle.close.format(2)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (lastCandle.close >= lastCandle.open) PositiveGreen else NegativeRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "H: ${lastCandle.high.format(2)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PositiveGreen,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "L: ${lastCandle.low.format(2)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NegativeRed,
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = "${candles.size} свечей",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    fontSize = 10.sp
                )
            }
        }

        // Candlestick chart
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 3f)
                        offsetX = (offsetX + pan.x).coerceIn(
                            -size.width.toFloat() * (scale - 1),
                            0f
                        )
                    }
                }
        ) {
            if (candles.isEmpty()) return@Canvas

            val width = size.width
            val height = size.height
            
            // Calculate price range
            val maxPrice = candles.maxOf { it.high }
            val minPrice = candles.minOf { it.low }
            val priceRange = maxPrice - minPrice
            
            if (priceRange == 0.0) return@Canvas
            
            // Calculate visible candles based on scale
            val visibleCandles = (candles.size / scale).toInt().coerceAtLeast(10)
            val startIndex = ((-offsetX / width) * candles.size).toInt()
                .coerceIn(0, (candles.size - visibleCandles).coerceAtLeast(0))
            val endIndex = (startIndex + visibleCandles).coerceAtMost(candles.size)
            
            val visibleCandlesList = candles.subList(startIndex, endIndex)
            val candleWidth = width / visibleCandlesList.size.toFloat()
            val wickWidth = 2f
            
            // Draw grid lines
            drawGridLines(height, minPrice, maxPrice, priceRange)
            
            // Draw candles
            visibleCandlesList.forEachIndexed { index, candle ->
                val x = index * candleWidth + candleWidth / 2
                
                drawCandle(
                    candle = candle,
                    x = x,
                    height = height,
                    candleWidth = candleWidth * 0.8f,
                    wickWidth = wickWidth,
                    minPrice = minPrice,
                    priceRange = priceRange
                )
            }
        }
    }
}

private fun DrawScope.drawGridLines(
    height: Float,
    minPrice: Double,
    maxPrice: Double,
    priceRange: Double
) {
    val gridColor = Color.White.copy(alpha = 0.1f)
    val lines = 5
    
    for (i in 0..lines) {
        val y = height * (i.toFloat() / lines)
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawCandle(
    candle: Candle,
    x: Float,
    height: Float,
    candleWidth: Float,
    wickWidth: Float,
    minPrice: Double,
    priceRange: Double
) {
    val isGreen = candle.close >= candle.open
    val color = if (isGreen) Color(0xFF22C55E) else Color(0xFFEF4444)
    
    // Calculate Y positions (inverted because canvas Y grows downward)
    val highY = height - ((candle.high - minPrice) / priceRange * height).toFloat()
    val lowY = height - ((candle.low - minPrice) / priceRange * height).toFloat()
    val openY = height - ((candle.open - minPrice) / priceRange * height).toFloat()
    val closeY = height - ((candle.close - minPrice) / priceRange * height).toFloat()
    
    // Draw wick (high-low line)
    drawLine(
        color = color,
        start = Offset(x, highY),
        end = Offset(x, lowY),
        strokeWidth = wickWidth
    )
    
    // Draw body (open-close rectangle)
    val bodyTop = min(openY, closeY)
    val bodyBottom = max(openY, closeY)
    val bodyHeight = bodyBottom - bodyTop
    
    if (bodyHeight < 1f) {
        // Doji candle (open == close)
        drawLine(
            color = color,
            start = Offset(x - candleWidth / 2, openY),
            end = Offset(x + candleWidth / 2, openY),
            strokeWidth = 2f
        )
    } else {
        // Normal candle
        drawRect(
            color = color,
            topLeft = Offset(x - candleWidth / 2, bodyTop),
            size = androidx.compose.ui.geometry.Size(candleWidth, bodyHeight)
        )
    }
}