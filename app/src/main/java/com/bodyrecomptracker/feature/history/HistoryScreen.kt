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
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(onBack: () -> Unit) {
	val ctx = LocalContext.current
	val db = AppDatabase.get(ctx)
	val todayEpoch = LocalDate.now().toEpochDay()
	val start14 = LocalDate.now().minusDays(13).toEpochDay()
	val meals14 = db.mealDao().observeMealsBetween(start14, todayEpoch).collectAsState(initial = emptyList()).value
	val sessions14 = db.workoutDao().observeSessionsBetween(start14, todayEpoch).collectAsState(initial = emptyList()).value

	LazyColumn(
		modifier = Modifier
			.fillMaxSize()
			.statusBarsPadding()
			.navigationBarsPadding()
			.imePadding()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		item { Text("Histórico (últimos 14 dias)") }
		items((0..13).map { todayEpoch - it }) { day ->
			val date = LocalDate.ofEpochDay(day)
			val fmt = date.format(DateTimeFormatter.ofPattern("dd/MM"))
			val mealsDay = meals14.filter { it.epochDay == day }
			val cal = mealsDay.sumOf { it.calories }
			val trainedSessions = sessions14.filter { it.epochDay == day }
			val trained = trainedSessions.isNotEmpty()
			Text("$fmt • Calorias: $cal • Treino: ${if (trained) trainedSessions.first().type else "—"}")
			Spacer(Modifier.height(4.dp))
		}
		item { Spacer(Modifier.height(12.dp)) }
		item { Button(onClick = onBack) { Text("Voltar") } }
	}
}

