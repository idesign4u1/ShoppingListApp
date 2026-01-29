package com.shoppinglist.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PieChart(
    data: Map<String, Double>,
    radiusOuter: Dp = 100.dp,
    chartBarWidth: Dp = 30.dp,
    animDuration: Int = 1000,
    modifier: Modifier = Modifier
) {
    val totalSum = data.values.sum()
    val floatValue = mutableListOf<Float>()

    // Calculate angles
    data.values.forEachIndexed { index, value ->
        floatValue.add(index, 360 * value.toFloat() / totalSum.toFloat())
    }

    // Define colors
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        Color.Magenta,
        Color.Yellow,
        Color.Cyan
    )

    var lastValue = 0f

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Chart
        Box(
            modifier = Modifier.size(radiusOuter * 2.5f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(radiusOuter * 2f)
            ) {
                floatValue.forEachIndexed { index, value ->
                    drawArc(
                        color = colors.getOrElse(index) { Color.Gray },
                        startAngle = lastValue,
                        sweepAngle = value,
                        useCenter = false,
                        style = Stroke(chartBarWidth.toPx())
                    )
                    lastValue += value
                }
            }
            
            // Total Center Text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "סה\"כ",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "₪${String.format("%.1f", totalSum)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Legend
        data.keys.forEachIndexed { index, key ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = colors.getOrElse(index) { Color.Gray },
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = key,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "₪${data[key]}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
