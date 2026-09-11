package com.talentdafrique.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val TalentColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Surface,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = Surface,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    background = Background,
    onBackground = OnPrimaryContainer,
    surface = Surface,
    onSurface = OnPrimaryContainer,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    error = Error,
    errorContainer = ErrorContainer,
    outline = Outline,
)

@Composable
fun TalentDAfriqueTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TalentColorScheme,
        typography = TalentTypography,
        shapes = TalentShapes,
        content = content,
    )
}