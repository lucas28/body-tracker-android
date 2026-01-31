package com.bodyrecomptracker.feature.meals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bodyrecomptracker.data.db.AppDatabase
import com.bodyrecomptracker.data.db.MealEntry
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun MealsScreen() {
	val ctx = LocalContext.current
	val scope = rememberCoroutineScope()
	val db = AppDatabase.get(ctx)
	val today = LocalDate.now().toEpochDay()
	val mealsToday by db.mealDao().observeMealsForDay(today).collectAsState(initial = emptyList())

	var mealName by remember { mutableStateOf("") }
	var mealKcal by remember { mutableStateOf("") }
	var mealProtein by remember { mutableStateOf("") }

	LazyColumn(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		item {
			Text("Refeições")
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
			Text("Hoje")
		}
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
}

