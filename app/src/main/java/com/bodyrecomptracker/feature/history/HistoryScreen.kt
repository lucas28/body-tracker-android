package com.bodyrecomptracker.feature.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bodyrecomptracker.data.db.AppDatabase
import java.time.LocalDate

@Composable
fun HistoryScreen(onBack: () -> Unit) {
	val ctx = LocalContext.current
	val db = AppDatabase.get(ctx)
	val today = LocalDate.now().toEpochDay()
	val mealsToday = db.mealDao().observeMealsForDay(today).collectAsState(initial = emptyList()).value
	val sessionsToday = db.workoutDao().observeSessionsForDay(today).collectAsState(initial = emptyList()).value

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text("Histórico de Progressão")
		Text("Refeições de hoje:")
		LazyColumn {
			items(mealsToday, key = { it.id }) { meal ->
				Text("- ${meal.name}: ${meal.calories} kcal, ${meal.proteinGrams.toInt()} g proteína")
				Spacer(Modifier.height(4.dp))
			}
		}
		Spacer(Modifier.height(12.dp))
		Text("Treinos de hoje: ${sessionsToday.size}")
		Button(onClick = onBack) { Text("Voltar") }
	}
}

