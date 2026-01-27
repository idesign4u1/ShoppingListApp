package com.shoppinglist.app.ui.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Standard animation durations following Material Design 3
 */
object AnimationDurations {
    const val Fast = 150
    const val Medium = 300
    const val Slow = 500
}

/**
 * Standard easing curves
 */
object AnimationEasing {
    val Standard = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val Emphasized = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
}

/**
 * Fade in/out animation
 */
@Composable
fun fadeInAnimation(
    durationMillis: Int = AnimationDurations.Medium
): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = AnimationEasing.Standard
        )
    )
}

@Composable
fun fadeOutAnimation(
    durationMillis: Int = AnimationDurations.Medium
): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = AnimationEasing.Standard
        )
    )
}

/**
 * Slide animations
 */
@Composable
fun slideInVerticallyAnimation(
    durationMillis: Int = AnimationDurations.Medium
): EnterTransition {
    return slideInVertically(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = AnimationEasing.EmphasizedDecelerate
        ),
        initialOffsetY = { it / 2 }
    ) + fadeIn(
        animationSpec = tween(durationMillis = durationMillis)
    )
}

@Composable
fun slideOutVerticallyAnimation(
    durationMillis: Int = AnimationDurations.Medium
): ExitTransition {
    return slideOutVertically(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = AnimationEasing.EmphasizedAccelerate
        ),
        targetOffsetY = { it / 2 }
    ) + fadeOut(
        animationSpec = tween(durationMillis = durationMillis)
    )
}

/**
 * Scale animation for dialogs and popups
 */
@Composable
fun scaleInAnimation(
    durationMillis: Int = AnimationDurations.Medium
): EnterTransition {
    return scaleIn(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = AnimationEasing.EmphasizedDecelerate
        ),
        initialScale = 0.8f
    ) + fadeIn(
        animationSpec = tween(durationMillis = durationMillis)
    )
}

@Composable
fun scaleOutAnimation(
    durationMillis: Int = AnimationDurations.Medium
): ExitTransition {
    return scaleOut(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = AnimationEasing.EmphasizedAccelerate
        ),
        targetScale = 0.8f
    ) + fadeOut(
        animationSpec = tween(durationMillis = durationMillis)
    )
}

/**
 * Expand/Collapse animations
 */
@Composable
fun expandVerticallyAnimation(
    durationMillis: Int = AnimationDurations.Medium
): EnterTransition {
    return expandVertically(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = AnimationEasing.EmphasizedDecelerate
        )
    ) + fadeIn(
        animationSpec = tween(durationMillis = durationMillis)
    )
}

@Composable
fun shrinkVerticallyAnimation(
    durationMillis: Int = AnimationDurations.Medium
): ExitTransition {
    return shrinkVertically(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = AnimationEasing.EmphasizedAccelerate
        )
    ) + fadeOut(
        animationSpec = tween(durationMillis = durationMillis)
    )
}

/**
 * Reusable spring animation spec
 */
fun <T> springAnimation(
    dampingRatio: Float = Spring.DampingRatioMediumBouncy,
    stiffness: Float = Spring.StiffnessMedium
): SpringSpec<T> {
    return spring(
        dampingRatio = dampingRatio,
        stiffness = stiffness
    )
}

/**
 * Reusable tween animation spec
 */
fun <T> tweenAnimation(
    durationMillis: Int = AnimationDurations.Medium,
    easing: Easing = AnimationEasing.Standard
): TweenSpec<T> {
    return tween(
        durationMillis = durationMillis,
        easing = easing
    )
}

/**
 * Infinite pulse animation for loading states
 */
@Composable
fun rememberInfinitePulseAnimation(): InfiniteTransition {
    return rememberInfiniteTransition(label = "pulse")
}

/**
 * Shake animation for errors
 */
fun shakeKeyframes(): KeyframesSpec<Float> {
    return keyframes {
        durationMillis = 400
        0f at 0
        -10f at 50
        10f at 100
        -10f at 150
        10f at 200
        -5f at 250
        5f at 300
        0f at 400
    }
}
