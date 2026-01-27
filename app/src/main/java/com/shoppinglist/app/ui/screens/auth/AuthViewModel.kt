package com.shoppinglist.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoppinglist.app.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            authRepository.authStateFlow().collect { user ->
                _uiState.value = _uiState.value.copy(isLoggedIn = user != null)
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = authRepository.signIn(email.trim(), password)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = translateError(e.message))
                }
            )
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
             _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = authRepository.signUp(email.trim(), password, displayName.trim())
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = translateError(e.message))
                }
            )
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.signInWithGoogle(account)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = translateError(e.message))
                }
            )
        }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "הזן כתובת אימייל")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.sendPasswordResetEmail(email.trim())
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, message = "נשלח אימייל לאיפוס סיסמה")
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = translateError(e.message))
                }
            )
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    private fun translateError(message: String?): String {
        return when {
            message == null -> "שגיאה לא ידועה"
            message.contains("email", ignoreCase = true) && message.contains("invalid", ignoreCase = true) -> "כתובת אימייל לא תקינה"
            message.contains("password", ignoreCase = true) && message.contains("weak", ignoreCase = true) -> "הסיסמה חלשה מדי"
            message.contains("email", ignoreCase = true) && message.contains("already", ignoreCase = true) -> "כתובת האימייל כבר רשומה"
            message.contains("user", ignoreCase = true) && message.contains("not found", ignoreCase = true) -> "משתמש לא נמצא"
            message.contains("password", ignoreCase = true) && message.contains("wrong", ignoreCase = true) -> "סיסמה שגויה"
            message.contains("network", ignoreCase = true) -> "בעיית תקשורת"
            else -> message
        }
    }
}
