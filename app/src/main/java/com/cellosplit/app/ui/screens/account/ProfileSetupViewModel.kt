package com.cellosplit.app.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cellosplit.app.data.local.prefs.UserPrefsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val dataStore: UserPrefsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
        validate()
    }

    fun updateUpiId(upiId: String) {
        _uiState.value = _uiState.value.copy(upiId = upiId)
        validate()
    }

    private fun validate() {
        val state = _uiState.value
        _uiState.value = state.copy(isValid = state.name.isNotBlank())
    }

    fun saveProfile(onComplete: () -> Unit) {
        val state = _uiState.value
        if (!state.isValid) return

        viewModelScope.launch {
            dataStore.setUserProfile(
                name = state.name.trim(),
                upiId = state.upiId.trim().takeIf { it.isNotEmpty() }
            )
            onComplete()
        }
    }
}

data class ProfileSetupUiState(
    val name: String = "",
    val upiId: String = "",
    val isValid: Boolean = false
)
