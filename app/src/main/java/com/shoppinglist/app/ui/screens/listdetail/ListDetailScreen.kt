package com.shoppinglist.app.ui.screens.listdetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoppinglist.app.data.model.Product
import com.shoppinglist.app.data.model.ProductCategories
import com.shoppinglist.app.data.model.ProductUnits

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    viewModel: ListDetailViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var showAssignDialog by remember { mutableStateOf<Product?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.shoppingList?.name ?: "טוען...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "חזור", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "אפשרויות", tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("צ'אט") },
                            leadingIcon = { Icon(Icons.Default.Chat, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onNavigateToChat()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("שתף רשימה") },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                showShareDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("מחק מוצרים שהושלמו") },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.deleteCompletedProducts()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddProductDialog = true },
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Icon(Icons.Default.Add, contentDescription = "הוסף מוצר")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.message != null) {
                Text(
                    text = uiState.message!!,
                    color = Color.Green,
                    modifier = Modifier.padding(8.dp)
                )
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(3000)
                    viewModel.clearMessage()
                }
            }

            if (uiState.products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("אין מוצרים ברשימה. הוסף את המוצר הראשון!")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Group by category could be added here, but flat list for now
                    items(uiState.products) { product ->
                        ProductItem(
                            product = product,
                            onToggle = { viewModel.toggleProduct(product) },
                            onDelete = { viewModel.deleteProduct(product.id) },
                            onAssign = {
                                showAssignDialog = product
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }

    if (showAssignDialog != null) {
        val product = showAssignDialog!!
        // In a real app, this would fetch list members. 
        // For this demo, we'll just allow typing a name/email or "Assign to me".
        AlertDialog(
            onDismissRequest = { showAssignDialog = null },
            title = { Text("שייך מוצר") },
            text = { Text("למי לשייך את ${product.name}?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.assignProduct(product, "me", "אני")
                    showAssignDialog = null
                }) {
                    Text("שייך לי")
                }
            },
            dismissButton = {
                 TextButton(onClick = { showAssignDialog = null }) {
                    Text("ביטול")
                }
            }
        )
    }

    if (showAddProductDialog) {
        val allCategories = (ProductCategories.list + uiState.categories).distinct()
        AddProductDialog(
            categories = allCategories,
            onDismiss = { showAddProductDialog = false },
            onConfirm = { name, quantity, unit, category, notes ->
                viewModel.addProduct(name, quantity, unit, category, notes)
                showAddProductDialog = false
            }
        )
    }

    if (showShareDialog) {
        ShareListDialog(
            onDismiss = { showShareDialog = false },
            onConfirm = { email ->
                viewModel.inviteUser(email)
                showShareDialog = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductItem(
    product: Product,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onAssign: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    ListItem(
        modifier = Modifier.combinedClickable(
            onClick = onToggle,
            onLongClick = { showDeleteDialog = true }
        ),
        headlineContent = {
            Text(
                text = product.name,
                textDecoration = if (product.isCompleted) TextDecoration.LineThrough else null,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        supportingContent = {
            Column {
                if (product.notes.isNotEmpty()) {
                    Text(text = product.notes, style = MaterialTheme.typography.bodySmall)
                }
                if (product.assignedToName != null) {
                    Text(
                        text = "משויך ל: ${product.assignedToName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        },
        leadingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = product.isCompleted,
                    onCheckedChange = { onToggle() },
                    enabled = true
                )
                IconButton(onClick = onAssign, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Person, 
                        contentDescription = "שייך",
                        tint = if (product.assignedToName != null) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            Color.Gray
                    )
                }
            }
        },
        trailingContent = {
            Text(
                text = "${product.quantity} ${product.unit}",
                style = MaterialTheme.typography.labelMedium
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = if (product.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) 
                             else MaterialTheme.colorScheme.surface
        )
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("מחק מוצר") },
            text = { Text("האם אתה בטוח שברצונך למחוק את ${product.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("מחק", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("ביטול")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf(ProductUnits.list.first()) }
    var category by remember { mutableStateOf(categories.firstOrNull() ?: "כללי") }
    var notes by remember { mutableStateOf("") }
    
    // Dropdown states
    var unitExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("מוצר חדש") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("שם המוצר") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { if (it.all { char -> char.isDigit() }) quantity = it },
                        label = { Text("כמות") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = unit,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("יחידה") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                            modifier = Modifier.clickable { unitExpanded = true }
                        )
                        DropdownMenu(
                            expanded = unitExpanded,
                            onDismissRequest = { unitExpanded = false }
                        ) {
                            ProductUnits.list.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        unit = item
                                        unitExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("קטגוריה") },
                        trailingIcon = { 
                            IconButton(onClick = { categoryExpanded = !categoryExpanded }) {
                                Icon(
                                    if (categoryExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "בחר קטגוריה"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    category = item
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // Allow custom category if not in list (OutlinedTextField is editable so user can just type)

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("הערות (אופציונלי)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val qtyInt = quantity.toIntOrNull() ?: 1
                    if (name.isNotBlank()) onConfirm(name, qtyInt, unit, category, notes) 
                },
                enabled = name.isNotBlank()
            ) {
                Text("הוסף")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ביטול")
            }
        }
    )
}

@Composable
fun ShareListDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("שתף רשימה") },
        text = {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("אימייל של המשתמש") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (email.isNotBlank()) onConfirm(email) },
                enabled = email.isNotBlank()
            ) {
                Text("שלח הזמנה")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ביטול")
            }
        }
    )
}
