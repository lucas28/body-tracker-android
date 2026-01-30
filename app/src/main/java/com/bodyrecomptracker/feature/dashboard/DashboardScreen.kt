package com.bodyrecomptracker.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bodyrecomptracker.data.db.AppDatabase
import com.bodyrecomptracker.data.db.MealEntry
import com.bodyrecomptracker.domain.Calculators
import com.bodyrecomptracker.domain.TmbInput
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.max

@Composable
fun DashboardScreen(
	onLogWorkout: () -> Unit,
	onHistory: () -> Unit,
	onSettings: () -> Unit
) {
	val ctx = LocalContext.current
	val scope = rememberCoroutineScope()
	val db = AppDatabase.get(ctx)
	val today = LocalDate.now().toEpochDay()

	val profile by db.bodyProfileDao().observeLatest().collectAsState(initial = null)
	val mealsToday by db.mealDao().observeMealsForDay(today).collectAsState(initial = emptyList())

	val bmr = profile?.let {
		Calculators.calculateBmr(TmbInput(it.weightKg, it.heightCm, it.ageYears, it.isMale))
	} ?: 0.0
	val tdee = if (bmr > 0) Calculators.calculateTdee(bmr, 1.2) else 0.0
	val targetCalories = if (tdee > 0) Calculators.targetCaloriesForDeficit(tdee, 500) else 0
	val totalsCalories = mealsToday.sumOf { it.calories }

	val (protLow, protHigh) = profile?.let { Calculators.proteinTargetRange(it.weightKg) } ?: (150 to 150)
	val targetProtein = max(150, protLow)
	val totalProtein = mealsToday.sumOf { it.proteinGrams }

	var mealName by remember { mutableStateOf("") }
	var mealKcal by remember { mutableStateOf("") }
	var mealProtein by remember { mutableStateOf("") }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text("Resumo do Dia")
		Text("Meta de déficit: -500 kcal")
		Text("Calorias: $totalsCalories / $targetCalories kcal")
		LinearProgressIndicator(progress = {
			if (targetCalories > 0) (totalsCalories.coerceAtMost(targetCalories).toFloat() / targetCalories.toFloat())
			else 0f
		})
		Text("Proteína: ${totalProtein.toInt()}g / ${targetProtein}g (faixa: $protLow-$protHigh)")
		LinearProgressIndicator(progress = {
			if (targetProtein > 0) (totalProtein.coerceAtMost(targetProtein.toDouble()).toFloat() / targetProtein.toFloat())
			else 0f
		})

		Spacer(Modifier.height(8.dp))
		Text("Adicionar refeição rápida")
		OutlinedTextField(mealName, { mealName = it }, label = { Text("Nome") })
		OutlinedTextField(mealKcal, { mealKcal = it }, label = { Text("Calorias (kcal)") })
		OutlinedTextField(mealProtein, { mealProtein = it }, label = { Text("Proteína (g)") })
		Button(onClick = {
			val kcal = mealKcal.toIntOrNull() ?: 0
			val prot = mealProtein.toDoubleOrNull() ?: 0.0
			if (mealName.isNotBlank() && kcal > 0) {
				scope.launch {
					db.mealDao().insert(
						MealEntry(
							epochDay = today,
							name = mealName,
							calories = kcal,
							proteinGrams = prot,
							carbsGrams = 0.0,
							fatGrams = 0.0
						)
					)
					mealName = ""
					mealKcal = ""
					mealProtein = ""
				}
			}
		}) { Text("Salvar refeição") }

		Spacer(Modifier.height(12.dp))
		Text("Refeições de hoje")
		LazyColumn {
			items(mealsToday, key = { it.id }) { meal ->
				Column {
					Text("${meal.name}: ${meal.calories} kcal, ${meal.proteinGrams.toInt()} g proteína")
					TextButton(onClick = { scope.launch { db.mealDao().deleteById(meal.id) } }) {
						Text("Excluir")
					}
				}
				Spacer(Modifier.height(8.dp))
			}
		}

		Spacer(Modifier.height(12.dp))
		Button(onClick = onLogWorkout) { Text("Registrar Treino") }
		Button(onClick = onHistory) { Text("Histórico") }
		Button(onClick = onSettings) { Text("Configurações") }
	}
}

