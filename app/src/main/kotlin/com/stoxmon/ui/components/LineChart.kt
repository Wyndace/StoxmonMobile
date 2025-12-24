package com.stoxmon.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stoxmon.domain.model.Candle
import com.stoxmon.ui.theme.*

@Composable
fun LineChart(
    candles: List<Candle>,
    modifier: Modifier = Modifier
) {
    if (candles.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(250.dp)
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
        val firstCandle = candles.firstOrNull()
        if (lastCandle != null && firstCandle != null) {
            val change = lastCandle.close - firstCandle.close
            val changePercent = (change / firstCandle.close) * 100
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${lastCandle.close.format(2)} ₽",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "${if (change >= 0) "+" else ""}${changePercent.format(2)}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (change >= 0) PositiveGreen else NegativeRed,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "${candles.size} точек",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    fontSize = 10.sp
                )
            }
        }

        // Line chart
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (candles.isEmpty()) return@Canvas

            val width = size.width
            val height = size.height
            
            // Calculate price range
            val maxPrice = candles.maxOf { it.close }
            val minPrice = candles.minOf { it.close }
            val priceRange = maxPrice - minPrice
            
            if (priceRange == 0.0) return@Canvas
            
            // Draw grid lines
            val gridColor = Color.White.copy(alpha = 0.1f)
            for (i in 0..5) {
                val y = height * (i.toFloat() / 5)
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
            }
            
            // Calculate points
            val points = candles.mapIndexed { index, candle ->
                val x = (index.toFloat() / (candles.size - 1)) * width
                val y = height - ((candle.close - minPrice) / priceRange * height).toFloat()
                Offset(x, y)
            }
            
            // Draw gradient fill under line
            val fillPath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, height)
                    points.forEach { point ->
                        lineTo(point.x, point.y)
                    }
                    lineTo(points.last().x, height)
                    close()
                }
            }
            
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PrimaryPurple.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                )
            )
            
            // Draw line
            val linePath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, points.first().y)
                    points.forEach { point ->
                        lineTo(point.x, point.y)
                    }
                }
            }
            
            drawPath(
                path = linePath,
                color = PrimaryPurple,
                style = Stroke(width = 3f)
            )
            
            // Draw points
            points.forEach { point ->
                drawCircle(
                    color = PrimaryPurple,
                    radius = 3f,
                    center = point
                )
            }
        }
    }
}
