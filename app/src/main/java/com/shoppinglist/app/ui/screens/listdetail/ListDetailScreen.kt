package com.shoppinglist.app.ui.screens.listdetail

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.speech.RecognizerIntent
import android.app.Activity
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoppinglist.app.data.model.CatalogItem
import com.shoppinglist.app.data.model.Product
import com.shoppinglist.app.data.model.ProductCategories
import com.shoppinglist.app.data.model.ProductStatus
import com.shoppinglist.app.data.model.ProductUnits
import com.shoppinglist.app.ui.components.BudgetTracker
import com.shoppinglist.app.ui.components.EmptyProductsState
import com.shoppinglist.app.ui.components.LoadingScreen
import com.shoppinglist.app.ui.components.SkeletonListItem
import com.shoppinglist.app.ui.components.PieChart

import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    viewModel: ListDetailViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var showAssignDialog by remember { mutableStateOf<Product?>(null) }
    var productToComplete by remember { mutableStateOf<Product?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showEditPriceDialog by remember { mutableStateOf<Product?>(null) }
    var showAnalyticsDialog by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            viewModel.enableLocationReminder()
        } else {
            // Permission denied
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            uiState.shoppingList?.name ?: "טוען...",
                            style = MaterialTheme.typography.titleLarge
                        )
                        uiState.shoppingList?.let { list ->
                            Text(
                                "סה\"כ: ${list.completedCount}/${list.itemCount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "חזור")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToChat) {
                        Icon(Icons.Default.Chat, contentDescription = "צ'אט")
                    }
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "אפשרויות")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("ניתוח הוצאות") },
                            leadingIcon = { Icon(Icons.Default.PieChart, null) },
                            onClick = {
                                showMenu = false
                                showAnalyticsDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("הגדר מיקום סופר (תזכורת)") },
                            leadingIcon = { Icon(Icons.Default.AddLocation, null) },
                            onClick = {
                                showMenu = false
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("הגדר תקציב") },
                            leadingIcon = { Icon(Icons.Default.AccountBalance, null) },
                            onClick = {
                                showMenu = false
                                showBudgetDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("שתף רשימה") },
                            leadingIcon = { Icon(Icons.Default.Share, null) },
                            onClick = {
                                showMenu = false
                                showShareDialog = true
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("מחק מוצרים שהושלמו") },
                            leadingIcon = { Icon(Icons.Default.DeleteSweep, null) },
                            onClick = {
                                showMenu = false
                                viewModel.deleteCompletedProducts()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddProductDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "הוסף מוצר")
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.products.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                repeat(5) {
                    SkeletonListItem()
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Budget Tracker Section
                uiState.shoppingList?.let { list ->
                    if (list.budget != null) {
                        BudgetTracker(
                            budget = list.budget,
                            totalSpent = list.totalSpent,
                            estimatedTotal = list.estimatedTotal,
                            currency = list.currency,
                            modifier = Modifier.padding(16.dp),
                            onSetBudget = { showBudgetDialog = true }
                        )
                    }
                }

                if (uiState.message != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = uiState.message!!,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(3000)
                        viewModel.clearMessage()
                    }
                }

                if (uiState.products.isEmpty()) {
                    EmptyProductsState(
                        onAddProduct = { showAddProductDialog = true },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        // Group products by category
                        val groupedProducts = uiState.products.groupBy { it.category }
                        
                        groupedProducts.forEach { (category, products) ->
                            item {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                            
                            items(products, key = { it.id }) { product ->
                                ProductItem(
                                    product = product,
                                    onToggle = { 
                                        if (!product.isCompleted) {
                                            productToComplete = product
                                        } else {
                                            viewModel.toggleProduct(product)
                                        }
                                    },
                                    onDelete = { viewModel.deleteProduct(product.id) },
                                    onAssign = { showAssignDialog = product },
                                    onEditPrice = { showEditPriceDialog = product }
                                )
                                Divider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.padding(start = 56.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAnalyticsDialog) {
        val expenses = viewModel.getExpensesByCategory()
        AlertDialog(
            onDismissRequest = { showAnalyticsDialog = false },
            title = { Text("ניתוח הוצאות") },
            text = {
                if (expenses.values.sum() > 0) {
                    PieChart(data = expenses)
                } else {
                    Text("אין נתונים להצגה (רכוש מוצרים כדי לראות ניתוח)")
                }
            },
            confirmButton = {
                TextButton(onClick = { showAnalyticsDialog = false }) {
                    Text("סגור")
                }
            }
        )
    }

    if (showAssignDialog != null) {
        val product = showAssignDialog!!
        val members = uiState.shoppingList?.memberEmails ?: emptyList()

        AlertDialog(
            onDismissRequest = { showAssignDialog = null },
            title = { Text("שייך מוצר") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("למי לשייך את ${product.name}?")
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Me option
                    OutlinedButton(
                        onClick = {
                            viewModel.assignProduct(product, "me", "אני")
                            showAssignDialog = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("אני")
                    }
                    
                    // Other members
                    members.forEach { email ->
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                viewModel.assignProduct(product, email, email.substringBefore("@"))
                                showAssignDialog = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(email)
                        }
                    }
                }
            },
            confirmButton = {},
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
            suggestions = uiState.catalogSuggestions,
            onSearch = { query -> viewModel.searchCatalog(query) },
            onDismiss = { showAddProductDialog = false },
            onConfirm = { name, quantity, unit, category, notes, price ->
                viewModel.addProduct(name, quantity, unit, category, notes, price)
                showAddProductDialog = false
            }
        )
    }
    
    // Simple Budget Dialog Implementation
    if (showBudgetDialog) {
        var budgetAmount by remember { mutableStateOf(uiState.shoppingList?.budget?.toString() ?: "") }
        
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            title = { Text("הגדרת תקציב") },
            text = {
                OutlinedTextField(
                    value = budgetAmount,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) budgetAmount = it },
                    label = { Text("סכום בש\"ח") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    val amount = budgetAmount.toDoubleOrNull()
                    if (amount != null) {
                        // This assumes setBudget exists in ViewModel (will need implementation)
                         viewModel.setBudget(amount)
                    }
                    showBudgetDialog = false
                }) {
                    Text("שמור")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = false }) {
                    Text("ביטול")
                }
            }
        )
    }

    if (showShareDialog) {
        ShareListDialog(
            onDismiss = { showShareDialog = false },
            onConfirm = { email ->
                viewModel.inviteUser(email)
                // Also trigger email intent for immediate action
                val emailIntent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                    data = android.net.Uri.parse("mailto:$email")
                    putExtra(android.content.Intent.EXTRA_SUBJECT, "הזמנה להצטרף לרשימת הקניות: ${uiState.shoppingList?.name}")
                    putExtra(android.content.Intent.EXTRA_TEXT, "היי,\n\nאני מזמין אותך להצטרף לרשימת הקניות שלי '${uiState.shoppingList?.name}' באפליקציית ה-Shopping List.\n\nבוא נערוך קניות ביחד!\n\n(אם האפליקציה מותקנת אצלך, ההזמנה כבר מחכה לך בפנים)")
                }
                try {
                    context.startActivity(emailIntent)
                } catch (e: Exception) {
                    // Fallback if no email client
                    android.widget.Toast.makeText(context, "לא נמצאה אפליקציית דואר", android.widget.Toast.LENGTH_SHORT).show()
                }
                showShareDialog = false
            },
            onShareLink = {
                // Share via generic system intent (WhatsApp, etc.)
                val sendIntent = android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    putExtra(android.content.Intent.EXTRA_TEXT, "היי! בוא להצטרף לרשימת הקניות שלי '${uiState.shoppingList?.name}' באפליקציה.")
                    type = "text/plain"
                }
                val shareIntent = android.content.Intent.createChooser(sendIntent, "הזמן חבר באמצעות...")
                context.startActivity(shareIntent)
            }
        )
    }

    if (productToComplete != null) {
        val product = productToComplete!!
        var priceInput by remember { mutableStateOf(product.price?.toString() ?: "") }
        
        AlertDialog(
            onDismissRequest = { productToComplete = null },
            title = { Text("כמה זה עלה?") },
            text = {
                Column {
                    Text("המוצר '${product.name}' סומן כנקנה.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) priceInput = it },
                        label = { Text("מחיר (אופציונלי)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        prefix = { Text("₪") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val price = priceInput.toDoubleOrNull() ?: 0.0
                    // Update price AND toggle completion status
                    viewModel.updateProductPriceAndToggle(product, price)
                    productToComplete = null
                }) {
                    Text("אישור")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToComplete = null }) {
                    Text("ביטול")
                }
            }
        )
    }

    if (showEditPriceDialog != null) {
        // ... (Existing EditPriceDialog code)
        val product = showEditPriceDialog!!
        var priceInput by remember { mutableStateOf(product.price?.toString() ?: "") }
        
        AlertDialog(
            onDismissRequest = { showEditPriceDialog = null },
            title = { Text("עדכן מחיר") },
            text = {
                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) priceInput = it },
                    label = { Text("מחיר ל-${product.unit}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    prefix = { Text("₪") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    val newPrice = priceInput.toDoubleOrNull()
                    if (newPrice != null) {
                        viewModel.updateProductPrice(product, newPrice)
                    }
                    showEditPriceDialog = null
                }) {
                    Text("עדכן")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditPriceDialog = null }) {
                    Text("ביטול")
                }
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
    onAssign: () -> Unit,
    onEditPrice: () -> Unit
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
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = if (product.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
            )
        },
        supportingContent = {
            Column {
                if (product.notes.isNotEmpty()) {
                    Text(text = product.notes, style = MaterialTheme.typography.bodySmall)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (product.price != null && product.price > 0) {
                         Text(
                            text = product.getDisplayPrice() ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (product.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    
                    if (product.assignedToName != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = product.assignedToName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    } else if (product.status == ProductStatus.CLAIMED) {
                        Text(
                            text = "🛒 מישהו קונה",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        },
        leadingContent = {
            Checkbox(
                checked = product.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${product.quantity} ${product.unit}",
                    style = MaterialTheme.typography.labelMedium,
                    textDecoration = if (product.isCompleted) TextDecoration.LineThrough else null
                )
                
                IconButton(onClick = onAssign, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.PersonAdd, 
                        contentDescription = "שייך",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
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
                    Text("מחק", color = MaterialTheme.colorScheme.error)
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
    suggestions: List<CatalogItem>,
    onSearch: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String, String, String, Double?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf(ProductUnits.list.first()) }
    var category by remember { mutableStateOf(categories.firstOrNull() ?: "כללי") }
    var notes by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var isPriceExpanded by remember { mutableStateOf(false) }
    
    // Dropdown states
    var unitExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var suggestionsExpanded by remember { mutableStateOf(false) }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (spokenText != null) {
                name = spokenText
                onSearch(spokenText)
            }
        }
    }

    LaunchedEffect(suggestions) {
        suggestionsExpanded = name.length >= 2 && suggestions.isNotEmpty()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("מוצר חדש") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                 // Name Input
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { 
                            name = it
                            onSearch(it)
                        },
                        label = { Text("שם המוצר") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        trailingIcon = {
                             IconButton(onClick = {
                                val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "he-IL")
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "דבר עכשיו...")
                                }
                                try {
                                    speechLauncher.launch(intent)
                                } catch (e: Exception) {
                                }
                            }) {
                                Icon(Icons.Default.Mic, contentDescription = "דיבור")
                            }
                        }
                    )
                    
                    DropdownMenu(
                        expanded = suggestionsExpanded,
                        onDismissRequest = { suggestionsExpanded = false },
                        properties = androidx.compose.ui.window.PopupProperties(focusable = false),
                        modifier = Modifier.fillMaxWidth(0.8f) 
                    ) {
                        suggestions.forEach { item ->
                            DropdownMenuItem(
                                text = { 
                                    Column {
                                        Text(item.name, fontWeight = FontWeight.Bold)
                                        if (item.estimatedPrice != null) {
                                            Text("מחיר משוער: ₪${item.estimatedPrice}", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                },
                                onClick = {
                                    name = item.name
                                    category = item.category
                                    if (item.defaultUnit.isNotEmpty()) unit = item.defaultUnit
                                    if (item.estimatedPrice != null) {
                                        price = item.estimatedPrice.toString()
                                        isPriceExpanded = true
                                    }
                                    suggestionsExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { if (it.all { char -> char.isDigit() }) quantity = it },
                        label = { Text("כמות") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = unit,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("יחידה") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                            modifier = Modifier.clickable { unitExpanded = true },
                            shape = MaterialTheme.shapes.medium
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
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
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

                // Price Section
                TextButton(onClick = { isPriceExpanded = !isPriceExpanded }) {
                    Text(if (isPriceExpanded) "הסר מחיר" else "הוסף מחיר (אופציונלי)")
                }
                
                if (isPriceExpanded) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) price = it },
                        label = { Text("מחיר ליחידה") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        prefix = { Text("₪") },
                        shape = MaterialTheme.shapes.medium
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("הערות (אופציונלי)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = MaterialTheme.shapes.medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        name,
                        quantity.toIntOrNull() ?: 1,
                        unit,
                        category,
                        notes,
                        price.toDoubleOrNull()
                    )
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
    onConfirm: (String) -> Unit,
    onShareLink: () -> Unit
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
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "הערה: המשתמש חייב להיות רשום לאפליקציה עם המייל הזה כדי לראות את ההזמנה.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Column(horizontalAlignment = Alignment.End) {
                Button(
                    onClick = { if (email.isNotBlank()) onConfirm(email) },
                    enabled = email.isNotBlank()
                ) {
                    Text("שלח הזמנה")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onShareLink) {
                    Icon(
                        Icons.Default.Share, 
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("שתף קישור")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ביטול")
            }
        }
    )
}
