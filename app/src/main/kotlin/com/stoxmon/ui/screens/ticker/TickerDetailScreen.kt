package com.stoxmon.ui.screens.ticker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stoxmon.domain.model.Candle
import com.stoxmon.domain.model.Dividend
import com.stoxmon.domain.model.Fundamentals
import com.stoxmon.ui.components.CandlestickChart
import com.stoxmon.ui.components.LineChart
import com.stoxmon.ui.components.CandlestickChart
import com.stoxmon.ui.components.LineChart
import com.stoxmon.ui.components.formatAmount
import com.stoxmon.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TickerDetailScreen(
    onBackClick: () -> Unit,
    viewModel: TickerDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    
    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.ticker,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Детали актива",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadTickerData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Обновить",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading && state.candles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryPurple)
            }
        } else if (state.error != null && state.candles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Ошибка загрузки",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = state.error ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Button(
                        onClick = { viewModel.loadTickerData() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryPurple
                        )
                    ) {
                        Text("Повторить")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    TimeframeSelector(
                        selectedTimeframe = state.selectedTimeframe,
                        onTimeframeSelected = { viewModel.changeTimeframe(it) }
                    )
                }
                
                item {
                    ChartCard(
                        candles = state.candles,
                        showCandleChart = state.showCandleChart,
                        onToggleChart = { viewModel.toggleChartType() }
                    )
                }
                
                item {
                    PriceCard(candles = state.candles)
                }
                
                if (state.fundamentals != null) {
                    item {
                        FundamentalsCard(fundamentals = state.fundamentals!!)
                    }
                }
                
                if (state.dividends.isNotEmpty()) {
                    item {
                        DividendsCard(dividends = state.dividends)
                    }
                }
            }
        }
    }
}

@Composable
fun TimeframeSelector(
    selectedTimeframe: String,
    onTimeframeSelected: (String) -> Unit
) {
    val timeframes = listOf("M1", "M10", "H1", "D1", "W1", "MN", "QN")
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundCard
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            timeframes.forEach { tf ->
                val isSelected = tf == selectedTimeframe
                Button(
                    onClick = { onTimeframeSelected(tf) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) PrimaryPurple else BackgroundDark,
                        contentColor = if (isSelected) TextPrimary else TextSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Text(
                        text = tf,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ChartCard(
    candles: List<Candle>,
    showCandleChart: Boolean,
    onToggleChart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundCard
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showCandleChart) "📊 Японские свечи" else "📈 Линейный график",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onToggleChart) {
                    Text(
                        text = "Переключить",
                        color = PrimaryPurple,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (candles.isNotEmpty()) {
                if (showCandleChart) {
                    CandlestickChart(candles = candles)
                } else {
                    LineChart(candles = candles)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${candles.size} свечей",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет данных",
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun PriceCard(candles: List<Candle>) {
    if (candles.isEmpty()) return
    
    val lastCandle = candles.last()
    val firstCandle = candles.first()
    val priceChange = lastCandle.close - firstCandle.close
    val priceChangePercent = (priceChange / firstCandle.close) * 100
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
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
                            CardGradientStart.copy(alpha = 0.5f),
                            CardGradientEnd.copy(alpha = 0.2f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Текущая цена",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = String.format("%.2f", lastCandle.close),
                        style = MaterialTheme.typography.displayMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "₽",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextSecondary
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoChip(label = "Open", value = String.format("%.2f", lastCandle.open))
                    InfoChip(label = "High", value = String.format("%.2f", lastCandle.high))
                    InfoChip(label = "Low", value = String.format("%.2f", lastCandle.low))
                }
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (priceChange >= 0) PositiveGreen else NegativeRed
                ) {
                    Text(
                        text = "${if (priceChange >= 0) "+" else ""}${String.format("%.2f", priceChangePercent)}% • ${if (priceChange >= 0) "+" else ""}${String.format("%.2f", priceChange)} ₽",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InfoChip(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun FundamentalsCard(fundamentals: Fundamentals) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundCard
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Фундаментальные показатели",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FundamentalItem(
                    label = "P/E",
                    value = fundamentals.pe?.let { String.format("%.2f", it) } ?: "—",
                    modifier = Modifier.weight(1f)
                )
                FundamentalItem(
                    label = "P/B",
                    value = fundamentals.pb?.let { String.format("%.2f", it) } ?: "—",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun FundamentalItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DividendsCard(dividends: List<Dividend>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundCard
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Дивиденды",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            
            dividends.take(5).forEach { dividend ->
                DividendItem(dividend = dividend)
            }
        }
    }
}

@Composable
fun DividendItem(dividend: Dividend) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dividend.date,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = "${String.format("%.2f", dividend.amount)} ${dividend.currency}",
            style = MaterialTheme.typography.bodyMedium,
            color = PositiveGreen,
            fontWeight = FontWeight.SemiBold
        )
    }
}
