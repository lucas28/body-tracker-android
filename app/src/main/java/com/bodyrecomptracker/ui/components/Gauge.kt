package com.bodyrecomptracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun Gauge(
	progress: Float,
	color: Color,
	background: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
	modifier: Modifier = Modifier.size(120.dp),
	stroke: Float = 14f
) {
	val p = progress.coerceIn(0f, 1f)
	Canvas(modifier = modifier) {
		val strokeWidth = stroke
		val diameter = min(size.width, size.height) - strokeWidth
		val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
		// fundo
		drawArc(
			color = background,
			startAngle = 135f,
			sweepAngle = 270f,
			useCenter = false,
			topLeft = topLeft,
			size = Size(diameter, diameter),
			style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
		)
		// progresso
		drawArc(
			color = color,
			startAngle = 135f,
			sweepAngle = 270f * p,
			useCenter = false,
			topLeft = topLeft,
			size = Size(diameter, diameter),
			style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
		)
	}
}

