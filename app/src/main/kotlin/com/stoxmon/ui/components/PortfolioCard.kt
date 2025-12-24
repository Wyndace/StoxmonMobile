package com.stoxmon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stoxmon.domain.model.Portfolio
import com.stoxmon.domain.model.PortfolioType
import com.stoxmon.ui.theme.*

@Composable
fun PortfolioCard(
    portfolio: Portfolio,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundCard
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            CardGradientStart.copy(alpha = 0.6f),
                            CardGradientEnd.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = portfolio.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• ${if (portfolio.type == PortfolioType.REAL) "real" else "fake"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (portfolio.type == PortfolioType.REAL) AccentPink else PrimaryPurple
                        )
                    }
                    
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = TextSecondary
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Total value
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = formatAmount(portfolio.totalValue),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = MaterialTheme.typography.displayMedium.fontSize
                        ),
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "/ вложено\n${formatAmount(portfolio.invested)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Через рост",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PercentBadge(
                            percent = portfolio.growthPercent,
                            amount = portfolio.growthAmount
                        )
                    }
                    
                    if (portfolio.payoutPercent != null && portfolio.payoutAmount != null) {
                        Column {
                            Text(
                                text = "Через выплаты",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            PercentBadge(
                                percent = portfolio.payoutPercent,
                                amount = portfolio.payoutAmount
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PercentBadge(
    percent: Double,
    amount: Double,
    modifier: Modifier = Modifier
) {
    val isPositive = percent >= 0
    val backgroundColor = if (isPositive) PositiveGreen else NegativeRed
    val sign = if (isPositive) "+" else ""
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Text(
            text = "$sign${percent.format(1)}% • $sign${formatAmount(amount)}",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

// Helper functions
fun formatAmount(amount: Double): String {
    return String.format("%.2f ₽", amount).replace(',', ' ')
}

fun Double.format(decimals: Int): String {
    return "%.${decimals}f".format(this)
}
