package com.shoppinglist.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Empty state component with icon, title, and description
 */
@Composable
fun EmptyStateComponent(
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.ShoppingCart,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionButton: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated icon
        val infiniteTransition = rememberInfiniteTransition(label = "empty_state")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )
        
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (actionButton != null) {
            Spacer(modifier = Modifier.height(24.dp))
            actionButton()
        }
    }
}

/**
 * Empty state for shopping lists
 */
@Composable
fun EmptyListsState(
    onCreateList: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateComponent(
        icon = Icons.Default.ShoppingCart,
        title = "אין רשימות קניות",
        description = "צור רשימת קניות ראשונה שלך והתחל לארגן את הקניות",
        modifier = modifier,
        actionButton = {
            Button(onClick = onCreateList) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("צור רשימה חדשה")
            }
        }
    )
}

/**
 * Empty state for products in a list
 */
@Composable
fun EmptyProductsState(
    onAddProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateComponent(
        icon = Icons.Default.AddShoppingCart,
        title = "הרשימה ריקה",
        description = "הוסף מוצרים לרשימה כדי להתחיל",
        modifier = modifier,
        actionButton = {
            FilledTonalButton(onClick = onAddProduct) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("הוסף מוצר")
            }
        }
    )
}

/**
 * Empty state for chat messages
 */
@Composable
fun EmptyChatState(
    modifier: Modifier = Modifier
) {
    EmptyStateComponent(
        icon = Icons.Default.Chat,
        title = "אין הודעות",
        description = "התחל שיחה עם חברי הרשימה",
        modifier = modifier
    )
}

/**
 * Empty search results
 */
@Composable
fun EmptySearchState(
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    EmptyStateComponent(
        icon = Icons.Default.SearchOff,
        title = "לא נמצאו תוצאות",
        description = "לא נמצאו תוצאות עבור \"$searchQuery\"",
        modifier = modifier
    )
}

/**
 * No internet connection state
 */
@Composable
fun NoConnectionState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateComponent(
        icon = Icons.Default.CloudOff,
        title = "אין חיבור לאינטרנט",
        description = "בדוק את החיבור שלך ונסה שוב",
        modifier = modifier,
        actionButton = {
            Button(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("נסה שוב")
            }
        }
    )
}
