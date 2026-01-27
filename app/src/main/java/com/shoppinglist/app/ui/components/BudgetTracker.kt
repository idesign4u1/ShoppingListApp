package com.shoppinglist.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shoppinglist.app.data.model.BudgetStatus
import com.shoppinglist.app.ui.theme.ShoppingColors

/**
 * Budget tracker component showing budget progress
 */
@Composable
fun BudgetTracker(
    budget: Double?,
    totalSpent: Double,
    estimatedTotal: Double,
    currency: String = "₪",
    modifier: Modifier = Modifier,
    onSetBudget: (() -> Unit)? = null
) {
    if (budget == null) {
        // No budget set - show prompt
        if (onSetBudget != null) {
            NoBudgetPrompt(
                onSetBudget = onSetBudget,
                modifier = modifier
            )
        }
    } else {
        // Budget set - show tracker
        BudgetProgressCard(
            budget = budget,
            totalSpent = totalSpent,
            estimatedTotal = estimatedTotal,
            currency = currency,
            modifier = modifier
        )
    }
}

@Composable
private fun NoBudgetPrompt(
    onSetBudget: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "הגדר תקציב לרשימה",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            FilledTonalButton(onClick = onSetBudget) {
                Text("הגדר")
            }
        }
    }
}

@Composable
private fun BudgetProgressCard(
    budget: Double,
    totalSpent: Double,
    estimatedTotal: Double,
    currency: String,
    modifier: Modifier = Modifier
) {
    val percentage = (totalSpent / budget * 100).toFloat().coerceIn(0f, 100f)
    val estimatedPercentage = (estimatedTotal / budget * 100).toFloat().coerceIn(0f, 100f)
    
    val budgetStatus = when {
        percentage >= 100 -> BudgetStatus.EXCEEDED
        percentage >= 80 -> BudgetStatus.WARNING
        else -> BudgetStatus.GOOD
    }
    
    val statusColor = when (budgetStatus) {
        BudgetStatus.EXCEEDED -> ShoppingColors.BudgetExceeded
        BudgetStatus.WARNING -> ShoppingColors.BudgetWarning
        BudgetStatus.GOOD -> ShoppingColors.BudgetGood
        BudgetStatus.NO_BUDGET -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val statusIcon = when (budgetStatus) {
        BudgetStatus.EXCEEDED -> Icons.Default.Warning
        BudgetStatus.WARNING -> Icons.Default.Info
        BudgetStatus.GOOD -> Icons.Default.CheckCircle
        BudgetStatus.NO_BUDGET -> Icons.Default.AccountBalance
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "תקציב",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = "$currency${String.format("%.2f", budget)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress bar
            AnimatedProgressBar(
                currentProgress = percentage / 100f,
                estimatedProgress = estimatedPercentage / 100f,
                color = statusColor,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BudgetStat(
                    label = "הוצאו",
                    value = "$currency${String.format("%.2f", totalSpent)}",
                    color = statusColor
                )
                
                if (estimatedTotal > totalSpent) {
                    BudgetStat(
                        label = "משוער",
                        value = "$currency${String.format("%.2f", estimatedTotal)}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                
                val remaining = budget - totalSpent
                BudgetStat(
                    label = if (remaining >= 0) "נותר" else "חריגה",
                    value = "$currency${String.format("%.2f", kotlin.math.abs(remaining))}",
                    color = if (remaining >= 0) 
                        MaterialTheme.colorScheme.onSurfaceVariant 
                    else 
                        ShoppingColors.BudgetExceeded
                )
            }
        }
    }
}

@Composable
private fun AnimatedProgressBar(
    currentProgress: Float,
    estimatedProgress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    var animatedProgress by remember { mutableStateOf(0f) }
    var animatedEstimated by remember { mutableStateOf(0f) }
    
    LaunchedEffect(currentProgress) {
        animatedProgress = currentProgress
    }
    
    LaunchedEffect(estimatedProgress) {
        animatedEstimated = estimatedProgress
    }
    
    val progress by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progress"
    )
    
    val estimated by animateFloatAsState(
        targetValue = animatedEstimated,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "estimated"
    )
    
    Box(
        modifier = modifier
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Estimated progress (lighter)
        if (estimated > progress) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(estimated.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(6.dp))
                    .background(color.copy(alpha = 0.3f))
            )
        }
        
        // Actual progress
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(6.dp))
                .background(color)
        )
    }
}

@Composable
private fun BudgetStat(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

/**
 * Compact budget indicator for list cards
 */
@Composable
fun CompactBudgetIndicator(
    budget: Double,
    totalSpent: Double,
    currency: String = "₪",
    modifier: Modifier = Modifier
) {
    val percentage = (totalSpent / budget * 100).toFloat()
    val color = when {
        percentage >= 100 -> ShoppingColors.BudgetExceeded
        percentage >= 80 -> ShoppingColors.BudgetWarning
        else -> ShoppingColors.BudgetGood
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AccountBalance,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = color
        )
        
        Text(
            text = "$currency${String.format("%.0f", totalSpent)}/$currency${String.format("%.0f", budget)}",
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}
