package com.stoxmon.ui.screens.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stoxmon.domain.model.Portfolio
import com.stoxmon.domain.usecase.GetPortfoliosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PortfolioUiState {
    object Loading : PortfolioUiState()
    data class Success(val portfolios: List<Portfolio>) : PortfolioUiState()
    data class Error(val message: String) : PortfolioUiState()
}

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val getPortfoliosUseCase: GetPortfoliosUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<PortfolioUiState>(PortfolioUiState.Loading)
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()
    
    init {
        loadPortfolios()
    }
    
    fun loadPortfolios() {
        viewModelScope.launch {
            _uiState.value = PortfolioUiState.Loading
            
            getPortfoliosUseCase()
                .onSuccess { portfolios ->
                    _uiState.value = PortfolioUiState.Success(portfolios)
                }
                .onFailure { error ->
                    _uiState.value = PortfolioUiState.Error(
                        error.message ?: "Неизвестная ошибка"
                    )
                }
        }
    }
}
