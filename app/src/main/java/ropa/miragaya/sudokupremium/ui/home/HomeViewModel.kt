package ropa.miragaya.sudokupremium.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ropa.miragaya.sudokupremium.domain.repository.GameRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: GameRepository
) : ViewModel() {

    val hasSavedGame: StateFlow<Boolean> = repository.getSavedGame()
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}