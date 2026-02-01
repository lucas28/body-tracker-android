package com.bodyrecomptracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun LineChart(
	points: List<Float>, // valores 0..1 normalizados (7 pontos)
	lineColor: Color,
	fillColor: Color = lineColor.copy(alpha = 0.2f)
) {
	Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
		if (points.isEmpty()) return@Canvas
		val stepX = size.width / (points.size - 1).coerceAtLeast(1)
		val path = Path()
		points.forEachIndexed { i, v ->
			val x = stepX * i
			val y = size.height - (v.coerceIn(0f, 1f) * size.height)
			if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
		}
		drawPath(path, color = lineColor, style = Stroke(width = 4f))
	}
}

