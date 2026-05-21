package com.jawnnypoo.openmeh.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import com.jawnnypoo.openmeh.model.ParsedTheme

@Composable
fun OpenMehTheme(
    parsedTheme: ParsedTheme? = null,
    content: @Composable () -> Unit,
) {
    val isDark = isSystemInDarkTheme()
    val colorScheme = remember(parsedTheme, isDark) {
        val accent = parsedTheme?.safeAccentColor()
        val foreground = parsedTheme?.safeForegroundColor()
        val background = parsedTheme?.safeBackgroundColor()
        if (accent == null || foreground == null || background == null) {
            if (isDark) {
                darkColorScheme()
            } else {
                lightColorScheme()
            }
        } else {
            if (isDark) {
                darkColorScheme(
                    primary = Color(accent),
                    onPrimary = Color(background),
                    background = Color(background),
                    onBackground = Color(foreground),
                    surface = Color(background),
                    onSurface = Color(foreground),
                )
            } else {
                lightColorScheme(
                    primary = Color(accent),
                    onPrimary = Color(background),
                    background = Color(background),
                    onBackground = Color(foreground),
                    surface = Color(background),
                    onSurface = Color(foreground),
                )
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
