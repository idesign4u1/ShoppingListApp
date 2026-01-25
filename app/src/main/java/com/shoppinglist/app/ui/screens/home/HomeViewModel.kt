package com.shoppinglist.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoppinglist.app.data.model.Invitation
import com.shoppinglist.app.data.model.ShoppingList
import com.shoppinglist.app.data.repository.AuthRepository
import com.shoppinglist.app.data.repository.InvitationRepository
import com.shoppinglist.app.data.repository.ShoppingListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val shoppingLists: List<ShoppingList> = emptyList(),
    val invitations: List<Invitation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val userEmail: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val shoppingListRepository: ShoppingListRepository,
    private val invitationRepository: InvitationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                userEmail = authRepository.currentUser?.email
            )

            launch {
                shoppingListRepository.getMyLists().collect { lists ->
                    _uiState.value = _uiState.value.copy(shoppingLists = lists)
                }
            }

            launch {
                invitationRepository.getMyInvitations().collect { invitations ->
                    _uiState.value = _uiState.value.copy(invitations = invitations)
                }
            }
        }
    }

    fun createList(name: String, description: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = shoppingListRepository.createList(name, description)
            result.onFailure {
                _uiState.value = _uiState.value.copy(error = it.message, isLoading = false)
            }
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, error = null)
            }
        }
    }

    fun duplicateList(originalList: ShoppingList, newName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = shoppingListRepository.duplicateList(originalList.id, newName)
            result.onFailure {
                _uiState.value = _uiState.value.copy(error = it.message, isLoading = false)
            }
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, error = null)
            }
        }
    }

    fun deleteList(listId: String) {
        viewModelScope.launch {
            shoppingListRepository.deleteList(listId)
        }
    }

    fun acceptInvitation(invitation: Invitation) {
        viewModelScope.launch {
            invitationRepository.acceptInvitation(invitation)
        }
    }

    fun declineInvitation(invitation: Invitation) {
        viewModelScope.launch {
            invitationRepository.declineInvitation(invitation)
        }
    }
    
    fun signOut() {
        authRepository.signOut()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
