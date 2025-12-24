package com.stoxmon.ui.screens.ticker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stoxmon.domain.model.*
import com.stoxmon.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TickerDetailState(
    val ticker: String = "",
    val candles: List<Candle> = emptyList(),
    val fundamentals: Fundamentals? = null,
    val dividends: List<Dividend> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val showCandleChart: Boolean = true,
    val selectedTimeframe: String = "M1"
)

@HiltViewModel
class TickerDetailViewModel @Inject constructor(
    private val getCandlesUseCase: GetTickerCandlesUseCase,
    private val getFundamentalsUseCase: GetTickerFundamentalsUseCase,
    private val getDividendsUseCase: GetTickerDividendsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val tickerSymbol: String = savedStateHandle["tickerSymbol"] ?: "ROSN"
    
    private val _uiState = MutableStateFlow(TickerDetailState(ticker = tickerSymbol))
    val uiState: StateFlow<TickerDetailState> = _uiState.asStateFlow()
    
    init {
        loadTickerData()
    }
    
    fun loadTickerData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // Загружаем все данные параллельно
            launch { loadCandles(_uiState.value.selectedTimeframe) }
            launch { loadFundamentals() }
            launch { loadDividends() }
        }
    }
    
    private suspend fun loadCandles(timeframe: String) {
        getCandlesUseCase(tickerSymbol, timeframe)
            .onSuccess { candles ->
                _uiState.value = _uiState.value.copy(
                    candles = candles,
                    isLoading = false
                )
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка загрузки свечей: ${error.message}",
                    isLoading = false
                )
            }
    }
    
    private suspend fun loadFundamentals() {
        getFundamentalsUseCase(tickerSymbol)
            .onSuccess { fundamentals ->
                _uiState.value = _uiState.value.copy(
                    fundamentals = fundamentals
                )
            }
            .onFailure { error ->
                // Не блокируем UI если фундаментальные показатели не загрузились
                println("Failed to load fundamentals: ${error.message}")
            }
    }
    
    private suspend fun loadDividends() {
        getDividendsUseCase(tickerSymbol)
            .onSuccess { dividends ->
                _uiState.value = _uiState.value.copy(
                    dividends = dividends
                )
            }
            .onFailure { error ->
                println("Failed to load dividends: ${error.message}")
            }
    }
    
    fun toggleChartType() {
        _uiState.value = _uiState.value.copy(
            showCandleChart = !_uiState.value.showCandleChart
        )
    }
    
    fun changeTimeframe(timeframe: String) {
        _uiState.value = _uiState.value.copy(selectedTimeframe = timeframe)
        viewModelScope.launch {
            loadCandles(timeframe)
        }
    }
}
