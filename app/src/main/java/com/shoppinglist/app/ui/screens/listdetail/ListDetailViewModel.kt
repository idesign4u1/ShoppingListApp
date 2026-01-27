package com.shoppinglist.app.ui.screens.listdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoppinglist.app.data.model.Product
import com.shoppinglist.app.data.model.ShoppingList
import com.shoppinglist.app.data.repository.CategoryRepository
import com.shoppinglist.app.data.repository.InvitationRepository
import com.shoppinglist.app.data.repository.ProductRepository
import com.shoppinglist.app.data.repository.ShoppingListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListDetailUiState(
    val shoppingList: ShoppingList? = null,
    val products: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class ListDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val shoppingListRepository: ShoppingListRepository,
    private val invitationRepository: InvitationRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val listId: String = checkNotNull(savedStateHandle["listId"])
    private val _uiState = MutableStateFlow(ListDetailUiState())
    val uiState: StateFlow<ListDetailUiState> = _uiState.asStateFlow()

    init {
        loadListDetails()
        loadProducts()
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getCustomCategories().onSuccess { cats ->
                _uiState.value = _uiState.value.copy(categories = cats)
            }
        }
    }

    private fun loadListDetails() {
        viewModelScope.launch {
            shoppingListRepository.getListById(listId).collect { list ->
                _uiState.value = _uiState.value.copy(shoppingList = list)
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            productRepository.getProductsForList(listId).collect { products ->
                _uiState.value = _uiState.value.copy(products = products)
                updateListCountsAndBudget(products)
            }
        }
    }

    private fun updateListCountsAndBudget(products: List<Product>) {
        viewModelScope.launch {
            // Calculate totals
            val totalItems = products.size
            val completedItems = products.count { it.isCompleted }
            val totalSpent = products.filter { it.isCompleted }.sumOf { it.getTotalPrice() ?: 0.0 }
            val estimatedTotal = products.sumOf { it.getTotalPrice() ?: 0.0 }

            // Update shopping list via repository
            shoppingListRepository.updateListMetadata(
                listId = listId,
                itemCount = totalItems,
                completedCount = completedItems,
                totalSpent = totalSpent,
                estimatedTotal = estimatedTotal
            )
        }
    }

    fun setBudget(amount: Double) {
        viewModelScope.launch {
            shoppingListRepository.updateBudget(listId, amount)
        }
    }

    fun addProduct(name: String, quantity: Int, unit: String, category: String, notes: String, price: Double? = null) {
        viewModelScope.launch {
            // Add category if new
            if (category !in _uiState.value.categories && category !in com.shoppinglist.app.data.model.ProductCategories.list) {
                categoryRepository.addCustomCategory(category)
                loadCategories()
            }
            
            val result = productRepository.addProduct(listId, name, quantity, unit, category, notes, price)
            result.onFailure {
                _uiState.value = _uiState.value.copy(error = it.message)
            }
        }
    }

    fun assignProduct(product: Product, userId: String?, userName: String?) {
        viewModelScope.launch {
            val updatedProduct = product.copy(
                assignedTo = userId,
                assignedToName = userName
            )
            productRepository.updateProduct(updatedProduct)
        }
    }

    fun toggleProduct(product: Product) {
        viewModelScope.launch {
            productRepository.toggleProductCompletion(product)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId)
        }
    }

    fun deleteCompletedProducts() {
        viewModelScope.launch {
            productRepository.deleteCompletedProducts(listId)
        }
    }

    fun inviteUser(email: String) {
        val listName = _uiState.value.shoppingList?.name ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null, error = null)
            val result = invitationRepository.sendInvitation(listId, listName, email)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, message = "הזמנה נשלחה בהצלחה")
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    fun updateProductPrice(product: Product, price: Double) {
        viewModelScope.launch {
            val updatedProduct = product.copy(price = price)
            productRepository.updateProduct(updatedProduct)
        }
    }
}
