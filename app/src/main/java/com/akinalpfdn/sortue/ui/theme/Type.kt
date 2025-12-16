package com.akinalpfdn.sortue.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.akinalpfdn.sortue.R

// Bundled Fredoka Font Family
// Since it's a variable font, we can just reference it.
// If it contains weight axes, FontWeight.Bold etc should work.
// If not, we might need to be explicit if we had separate files, but variable covers all.
val FredokaFontFamily = FontFamily(
    Font(R.font.fredoka)
)

// Set of Material typography styles to start with
// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FredokaFontFamily,
        fontWeight = FontWeight.Bold, // Increased from Normal
        fontSize = 64.sp, // Increased from 57
        lineHeight = 70.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FredokaFontFamily,
        fontWeight = FontWeight.Bold, // Increased from Normal
        fontSize = 48.sp, // Increased from 45
        lineHeight = 56.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FredokaFontFamily,
        fontWeight = FontWeight.Bold, // Increased from Normal
        fontSize = 40.sp, // Increased from 36
        lineHeight = 48.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FredokaFontFamily,
        fontWeight = FontWeight.Bold, // Increased from Normal
        fontSize = 36.sp, // Increased from 32
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FredokaFontFamily,
        fontWeight = FontWeight.SemiBold, // Increased from Normal
        fontSize = 32.sp, // Increased from 28
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FredokaFontFamily,
        fontWeight = FontWeight.SemiBold, // Increased from Normal
        fontSize = 28.sp, // Increased from 24
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FredokaFontFamily,
        fontWeight = FontWeight.SemiBold, // Increased from Normal
        fontSize = 26.sp, // Increased from 22
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FredokaFontFamily,
        fontWeight = FontWeight.SemiBold, // Increased from Medium
        fontSize = 20.sp, // Increased from 16
        lineHeight = 28.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FredokaFontFamily,
        fontWeight = FontWeight.Bold, // Increased from Medium
        fontSize = 18.sp, // Increased from 14
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FredokaFontFamily,
        fontWeight = FontWeight.Medium, // Increased from Normal
        fontSize = 18.sp, // Increased from 16
        lineHeight = 26.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FredokaFontFamily,
        fontWeight = FontWeight.Medium, // Increased from Normal
        fontSize = 16.sp, // Increased from 14
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FredokaFontFamily,
        fontWeight = FontWeight.Medium, // Increased from Normal
        fontSize = 14.sp, // Increased from 12
        lineHeight = 18.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FredokaFontFamily,
        fontWeight = FontWeight.SemiBold, // Increased from Medium
        fontSize = 16.sp, // Increased from 14
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FredokaFontFamily,
        fontWeight = FontWeight.SemiBold, // Increased from Medium
        fontSize = 14.sp, // Increased from 12
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FredokaFontFamily,
        fontWeight = FontWeight.Bold, // Increased from Medium
        fontSize = 13.sp, // Increased from 11
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    )
)