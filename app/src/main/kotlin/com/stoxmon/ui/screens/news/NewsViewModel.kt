package com.stoxmon.ui.screens.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stoxmon.domain.model.News
import com.stoxmon.domain.usecase.GetNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NewsUiState {
    object Loading : NewsUiState()
    data class Success(val news: List<News>) : NewsUiState()
    data class Error(val message: String) : NewsUiState()
}

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()
    
    private var currentTicker = "ROSN"
    
    init {
        loadNews(currentTicker)
    }
    
    fun loadNews(ticker: String = currentTicker) {
        currentTicker = ticker
        viewModelScope.launch {
            _uiState.value = NewsUiState.Loading
            
            getNewsUseCase(ticker)
                .onSuccess { news ->
                    _uiState.value = NewsUiState.Success(news)
                }
                .onFailure { error ->
                    _uiState.value = NewsUiState.Error(
                        error.message ?: "Ошибка загрузки новостей"
                    )
                }
        }
    }
}
