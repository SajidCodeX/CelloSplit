package com.cellosplit.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cellosplit.app.core.utils.DateUtils
import com.cellosplit.app.domain.usecase.GetGroupsUseCase
import com.cellosplit.app.domain.usecase.CreateGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getGroupsUseCase: GetGroupsUseCase,
    private val createGroupUseCase: CreateGroupUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = getGroupsUseCase()
        .map { groups ->
            HomeUiState.Success(
                groups = groups.map { group ->
                    GroupUiModel(
                        id = group.id,
                        name = group.name,
                        subtitle = "Since ${DateUtils.formatMillisToDateString(group.createdAt)}",
                        initials = group.name.take(2).uppercase(),
                        balance = "0", // Computed via settlement engine in next phase
                        isPositive = true
                    )
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Loading
        )

    fun createTestGroup() {
        viewModelScope.launch {
            createGroupUseCase(
                name = "Test Group ${System.currentTimeMillis() % 1000}",
                description = "Auto-generated for testing",
                memberNames = listOf("You", "Alice", "Bob")
            )
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val groups: List<GroupUiModel>) : HomeUiState()
}
