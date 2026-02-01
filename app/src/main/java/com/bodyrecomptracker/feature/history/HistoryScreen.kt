package com.bodyrecomptracker.feature.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bodyrecomptracker.data.db.AppDatabase
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import com.bodyrecomptracker.domain.Calculators
import com.bodyrecomptracker.domain.TmbInput

@Composable
fun HistoryScreen(onBack: () -> Unit) {
	val ctx = LocalContext.current
	val db = AppDatabase.get(ctx)

	// Perfil e meta calórica
	val profile by db.bodyProfileDao().observeLatest().collectAsState(initial = null)
	val settings by db.appSettingsDao().observe().collectAsState(initial = null)
	val bmr = profile?.let { Calculators.calculateBmr(TmbInput(it.weightKg, it.heightCm, it.ageYears, it.isMale)) } ?: 0.0
	val tdee = if (bmr > 0) Calculators.calculateTdee(bmr, 1.2) else 0.0
	val deficit = settings?.deficitTargetKcal ?: 500
	val targetCalories = kotlin.math.max(1200, (tdee - deficit).toInt())

	// Mês atual com navegação
	var currentMonth by remember { mutableStateOf(YearMonth.now()) }
	val firstDay = currentMonth.atDay(1)
	val lastDay = currentMonth.atEndOfMonth()
	val startEpoch = firstDay.toEpochDay()
	val endEpoch = lastDay.toEpochDay()

	val mealsMonth = db.mealDao().observeMealsBetween(startEpoch, endEpoch).collectAsState(initial = emptyList()).value
	val sessionsMonth = db.workoutDao().observeSessionsBetween(startEpoch, endEpoch).collectAsState(initial = emptyList()).value

	LazyColumn(
		modifier = Modifier
			.fillMaxSize()
			.statusBarsPadding()
			.navigationBarsPadding()
			.imePadding()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		item {
			Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				Button(onClick = { currentMonth = currentMonth.minusMonths(1) }) { Text("< Mês") }
				Text(currentMonth.atDay(1).format(DateTimeFormatter.ofPattern("MMMM yyyy")))
				Button(onClick = { currentMonth = currentMonth.plusMonths(1) }) { Text("Mês >") }
			}
		}
		item {
			MonthCalendar(
				month = currentMonth,
				targetCalories = targetCalories,
				meals = mealsMonth,
				sessions = sessionsMonth
			)
		}
		item { Button(onClick = onBack) { Text("Voltar") } }
	}
}

@Composable
private fun MonthCalendar(
	month: YearMonth,
	targetCalories: Int,
	meals: List<com.bodyrecomptracker.data.db.MealEntry>,
	sessions: List<com.bodyrecomptracker.data.db.WorkoutSession>
) {
	val first = month.atDay(1)
	val last = month.atEndOfMonth()
	val daysInMonth = last.dayOfMonth
	val startDowIndex = (first.dayOfWeek.value % 7) // 0=Sunday-like grid (Mon=1 → 1 mod 7)

	// Adesão: calorias <= meta; treino: se há sessão
	val caloriesByDay = (1..daysInMonth).associateWith { d ->
		val epoch = month.atDay(d).toEpochDay()
		meals.filter { it.epochDay == epoch }.sumOf { it.calories }
	}
	val trainedByDay = (1..daysInMonth).associateWith { d ->
		val epoch = month.atDay(d).toEpochDay()
		sessions.any { it.epochDay == epoch }
	}
	val adheredDays = (1..daysInMonth).count { (caloriesByDay[it] ?: 0) <= targetCalories }
	val workoutDays = trainedByDay.values.count { it }

	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		// Cabeçalho dias da semana
		Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
			listOf("S", "T", "Q", "Q", "S", "S", "D").forEach {
				Box(modifier = Modifier.size(36.dp)) { Text(it) }
			}
		}
		// Grid
		val cells = mutableListOf<Int?>()
		repeat(startDowIndex) { cells.add(null) }
		(1..daysInMonth).forEach { cells.add(it) }
		while (cells.size % 7 != 0) cells.add(null)

		for (rowStart in cells.indices step 7) {
			Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
				for (c in 0 until 7) {
					val day = cells[rowStart + c]
					if (day == null) {
						Box(modifier = Modifier.size(36.dp))
					} else {
						val cal = caloriesByDay[day] ?: 0
						val inTarget = cal <= targetCalories
						val trained = trainedByDay[day] == true
						val bg = when {
							inTarget && trained -> Color(0xFF2E7D32)
							inTarget -> Color(0xFF388E3C)
							trained -> Color(0xFF1976D2)
							else -> Color(0xFF424242)
						}
						Box(
							modifier = Modifier
								.size(36.dp)
								.background(bg, RoundedCornerShape(6.dp))
						) {
							Text(day.toString(), modifier = Modifier.padding(6.dp), color = Color.White)
						}
					}
				}
			}
		}
		Spacer(Modifier.height(8.dp))
		Text("Aderência (kcal ≤ meta): ${((adheredDays * 100f) / daysInMonth).toInt()}% • Dias com treino: $workoutDays/$daysInMonth")
	}
}

