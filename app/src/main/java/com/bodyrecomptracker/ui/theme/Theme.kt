package com.bodyrecomptracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColors: ColorScheme = darkColorScheme(
	primary = AccentPrimary,
	secondary = AccentProgress,
	background = DarkBackground,
	surface = DarkSurface,
	onPrimary = DarkBackground,
	onSecondary = DarkBackground,
	onBackground = OnDark,
	onSurface = OnDark
)

@Composable
fun BodyRecompTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	content: @Composable () -> Unit
) {
	MaterialTheme(
		colorScheme = DarkColors,
		typography = Typography,
		content = content
	)
}

