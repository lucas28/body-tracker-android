package com.bodyrecomptracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun AppCard(
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(16.dp),
	content: @Composable () -> Unit
) {
	val gradient = Brush.verticalGradient(
		colors = listOf(
			MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
			MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
		)
	)
	Column(
		modifier = modifier
			.clip(RoundedCornerShape(16.dp))
			.background(gradient)
			.padding(contentPadding)
	) {
		content()
	}
}

