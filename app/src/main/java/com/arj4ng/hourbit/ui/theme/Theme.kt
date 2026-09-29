package com.arj4ng.hourbit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/*
  ╔════════════════════════════════════════════════════════╗
  ║  █████╗ ██████╗      ██╗ ██╗  ██╗ ███╗   ██╗  ██████╗  ║
  ║ ██╔══██╗██╔══██╗     ██║ ██║  ██║ ████╗  ██║ ██╔════╝  ║
  ║ ███████║██████╔╝     ██║ ███████║ ██╔██╗ ██║ ██║  ███╗ ║
  ║ ██╔══██║██╔══██╗██   ██║ ╚════██║ ██║╚██╗██║ ██║   ██║ ║
  ║ ██║  ██║██║  ██║╚█████╔╝      ██║ ██║ ╚████║ ╚██████╔╝ ║
  ║ ╚═╝  ╚═╝╚═╝  ╚═╝ ╚════╝       ╚═╝ ╚═╝  ╚═══╝  ╚═════╝  ║
  ╚═════════════════════════════════════════ [ DEV TAG ] ══╝
*/


// Dark text placed on the light-blue primary color
private val ColorDarkPrimaryText =
    androidx.compose.ui.graphics.Color(0xFF102052)

private val LightColorScheme = lightColorScheme(

    primary = HourbitBlue,
    onPrimary = LightSurface,

    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,

    secondary = LightTextSecondary,
    onSecondary = LightSurface,

    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightText,

    tertiary = HourbitAmber,
    onTertiary = LightSurface,

    tertiaryContainer = LightBreakContainer,
    onTertiaryContainer = LightOnBreakContainer,

    background = LightBackground,
    onBackground = LightText,

    surface = LightSurface,
    onSurface = LightText,

    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,

    outline = LightOutline,
    outlineVariant = LightOutline,

    error = HourbitError,
    onError = LightSurface
)

private val DarkColorScheme = darkColorScheme(

    primary = HourbitBlueDark,
    onPrimary = ColorDarkPrimaryText,

    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,

    secondary = DarkTextSecondary,
    onSecondary = DarkBackground,

    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = DarkText,

    tertiary = HourbitAmberDark,
    onTertiary = DarkBackground,

    tertiaryContainer = DarkBreakContainer,
    onTertiaryContainer = DarkOnBreakContainer,

    background = DarkBackground,
    onBackground = DarkText,

    surface = DarkSurface,
    onSurface = DarkText,

    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,

    outline = DarkOutline,
    outlineVariant = DarkOutline,

    error = HourbitErrorDark,
    onError = DarkBackground
)

@Composable
fun HourbitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colors =
        if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        }

    MaterialTheme(
        colorScheme = colors,
        typography = HourbitTypography,
        content = content
    )
}
