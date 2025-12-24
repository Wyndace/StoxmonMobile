package com.stoxmon.ui.screens.ticker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stoxmon.domain.model.Ticker
import com.stoxmon.domain.usecase.GetPortfolioTickersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TickerListUiState {
    object Loading : TickerListUiState()
    data class Success(val tickers: List<Ticker>) : TickerListUiState()
    data class Error(val message: String) : TickerListUiState()
}

@HiltViewModel
class TickerViewModel @Inject constructor(
    private val getPortfolioTickersUseCase: GetPortfolioTickersUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val portfolioId: String = savedStateHandle["portfolioId"] ?: "1"
    
    private val _uiState = MutableStateFlow<TickerListUiState>(TickerListUiState.Loading)
    val uiState: StateFlow<TickerListUiState> = _uiState.asStateFlow()
    
    init {
        loadTickers()
    }
    
    fun loadTickers() {
        viewModelScope.launch {
            _uiState.value = TickerListUiState.Loading
            
            getPortfolioTickersUseCase(portfolioId)
                .onSuccess { tickers ->
                    _uiState.value = TickerListUiState.Success(tickers)
                }
                .onFailure { error ->
                    _uiState.value = TickerListUiState.Error(
                        error.message ?: "Ошибка загрузки тикеров"
                    )
                }
        }
    }
}
