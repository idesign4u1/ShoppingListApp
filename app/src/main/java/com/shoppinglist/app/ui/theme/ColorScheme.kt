package com.shoppinglist.app.ui.theme

import androidx.compose.ui.graphics.Color

// Light Theme Colors - Material Design 3
object LightColors {
    // Primary colors
    val Primary = Color(0xFF6750A4)
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Color(0xFFEADDFF)
    val OnPrimaryContainer = Color(0xFF21005D)
    
    // Secondary colors
    val Secondary = Color(0xFF625B71)
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFFE8DEF8)
    val OnSecondaryContainer = Color(0xFF1D192B)
    
    // Tertiary colors
    val Tertiary = Color(0xFF7D5260)
    val OnTertiary = Color(0xFFFFFFFF)
    val TertiaryContainer = Color(0xFFFFD8E4)
    val OnTertiaryContainer = Color(0xFF31111D)
    
    // Error colors
    val Error = Color(0xFFB3261E)
    val OnError = Color(0xFFFFFFFF)
    val ErrorContainer = Color(0xFFF9DEDC)
    val OnErrorContainer = Color(0xFF410E0B)
    
    // Background colors
    val Background = Color(0xFFFFFBFE)
    val OnBackground = Color(0xFF1C1B1F)
    
    // Surface colors
    val Surface = Color(0xFFFFFBFE)
    val OnSurface = Color(0xFF1C1B1F)
    val SurfaceVariant = Color(0xFFE7E0EC)
    val OnSurfaceVariant = Color(0xFF49454F)
    
    // Outline
    val Outline = Color(0xFF79747E)
    val OutlineVariant = Color(0xFFCAC4D0)
    
    // Other
    val Scrim = Color(0xFF000000)
    val InverseSurface = Color(0xFF313033)
    val InverseOnSurface = Color(0xFFF4EFF4)
    val InversePrimary = Color(0xFFD0BCFF)
    
    // Custom colors for shopping app
    val Success = Color(0xFF4CAF50)
    val OnSuccess = Color(0xFFFFFFFF)
    val SuccessContainer = Color(0xFFC8E6C9)
    
    val Warning = Color(0xFFFF9800)
    val OnWarning = Color(0xFF000000)
    val WarningContainer = Color(0xFFFFE0B2)
    
    val Info = Color(0xFF2196F3)
    val OnInfo = Color(0xFFFFFFFF)
    val InfoContainer = Color(0xFFBBDEFB)
}

// Dark Theme Colors - Material Design 3
object DarkColors {
    // Primary colors
    val Primary = Color(0xFFD0BCFF)
    val OnPrimary = Color(0xFF381E72)
    val PrimaryContainer = Color(0xFF4F378B)
    val OnPrimaryContainer = Color(0xFFEADDFF)
    
    // Secondary colors
    val Secondary = Color(0xFFCCC2DC)
    val OnSecondary = Color(0xFF332D41)
    val SecondaryContainer = Color(0xFF4A4458)
    val OnSecondaryContainer = Color(0xFFE8DEF8)
    
    // Tertiary colors
    val Tertiary = Color(0xFFEFB8C8)
    val OnTertiary = Color(0xFF492532)
    val TertiaryContainer = Color(0xFF633B48)
    val OnTertiaryContainer = Color(0xFFFFD8E4)
    
    // Error colors
    val Error = Color(0xFFF2B8B5)
    val OnError = Color(0xFF601410)
    val ErrorContainer = Color(0xFF8C1D18)
    val OnErrorContainer = Color(0xFFF9DEDC)
    
    // Background colors
    val Background = Color(0xFF1C1B1F)
    val OnBackground = Color(0xFFE6E1E5)
    
    // Surface colors
    val Surface = Color(0xFF1C1B1F)
    val OnSurface = Color(0xFFE6E1E5)
    val SurfaceVariant = Color(0xFF49454F)
    val OnSurfaceVariant = Color(0xFFCAC4D0)
    
    // Outline
    val Outline = Color(0xFF938F99)
    val OutlineVariant = Color(0xFF49454F)
    
    // Other
    val Scrim = Color(0xFF000000)
    val InverseSurface = Color(0xFFE6E1E5)
    val InverseOnSurface = Color(0xFF313033)
    val InversePrimary = Color(0xFF6750A4)
    
    // Custom colors for shopping app
    val Success = Color(0xFF81C784)
    val OnSuccess = Color(0xFF003300)
    val SuccessContainer = Color(0xFF1B5E20)
    
    val Warning = Color(0xFFFFB74D)
    val OnWarning = Color(0xFF331A00)
    val WarningContainer = Color(0xFFE65100)
    
    val Info = Color(0xFF64B5F6)
    val OnInfo = Color(0xFF001A33)
    val InfoContainer = Color(0xFF0D47A1)
}

// Shopping-specific semantic colors
object ShoppingColors {
    // Product status colors
    val ProductPending = Color(0xFFFFB74D)
    val ProductClaimed = Color(0xFF64B5F6)
    val ProductInProgress = Color(0xFF9575CD)
    val ProductCompleted = Color(0xFF81C784)
    
    // Category colors (can be used for category badges)
    val CategoryGeneral = Color(0xFF9E9E9E)
    val CategoryVegetables = Color(0xFF8BC34A)
    val CategoryDairy = Color(0xFF64B5F6)
    val CategoryMeat = Color(0xFFE57373)
    val CategoryBakery = Color(0xFFFFB74D)
    val CategoryBeverages = Color(0xFF4DD0E1)
    val CategorySnacks = Color(0xFFBA68C8)
    val CategoryCleaning = Color(0xFF4FC3F7)
    val CategoryPersonalCare = Color(0xFFFF8A65)
    val CategoryFrozen = Color(0xFF90CAF9)
    val CategoryCanned = Color(0xFFA1887F)
    val CategorySpices = Color(0xFFDCE775)
    
    // Budget colors
    val BudgetGood = Color(0xFF4CAF50)
    val BudgetWarning = Color(0xFFFF9800)
    val BudgetExceeded = Color(0xFFF44336)
}
