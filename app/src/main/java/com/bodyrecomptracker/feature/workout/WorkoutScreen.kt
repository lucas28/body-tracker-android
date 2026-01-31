package com.bodyrecomptracker.feature.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bodyrecomptracker.data.db.AppDatabase
import com.bodyrecomptracker.data.db.ExerciseSet
import com.bodyrecomptracker.data.db.WorkoutSession
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ExerciseInput(val name: String, var weightKg: String = "", var reps: String = "10")

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun WorkoutScreen(onBack: () -> Unit) {
	val defaultExercises = listOf(
		"Supino reto", "Remada curvada", "Agachamento livre", "Levantamento terra", "Desenvolvimento militar"
	)
	val inputs = remember { mutableStateListOf<ExerciseInput>().apply { addAll(defaultExercises.map { ExerciseInput(it) }) } }
	val ctx = LocalContext.current
	val scope = rememberCoroutineScope()
	val db = AppDatabase.get(ctx)

	var currentDate by remember { mutableStateOf(LocalDate.now()) }
	val epochDay get() = currentDate.toEpochDay()

	LaunchedEffect(Unit) {
		// Pré-preenche com última carga conhecida por exercício
		defaultExercises.forEachIndexed { index, name ->
			val last = db.workoutDao().findLastSetForExercise(name)
			if (last != null) {
				inputs[index].weightKg = if (last.weightKg > 0.0) last.weightKg.toString() else ""
				inputs[index].reps = if (last.reps > 0) last.reps.toString() else "10"
			}
		}
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.statusBarsPadding()
			.navigationBarsPadding()
			.imePadding()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text("Registro de Treino (PPL) — ${currentDate}")
		// Navegação por dia
		Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
			Button(onClick = { currentDate = currentDate.minusDays(1) }) { Text("< Dia anterior") }
			Button(onClick = { currentDate = currentDate.plusDays(1) }) { Text("Próximo dia >") }
		}
		LazyColumn(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			items(inputs, key = { it.name }) { item ->
				Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
					Text(item.name)
					// Dropdown de cargas (0..300 de 5 em 5)
					var expanded by remember { mutableStateOf(false) }
					ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
						OutlinedTextField(
							readOnly = true,
							value = item.weightKg.ifBlank { "Selecionar carga (kg)" },
							onValueChange = {},
							label = { Text("Carga (kg)") },
							trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
							modifier = Modifier.menuAnchor()
						)
						val options = (0..300 step 5).map { it.toString() }
						androidx.compose.material3.ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
							options.forEach { opt ->
								DropdownMenuItem(
									text = { Text("$opt kg") },
									onClick = {
										item.weightKg = opt
										expanded = false
									}
								)
							}
						}
					}
					OutlinedTextField(value = item.reps, onValueChange = { item.reps = it }, label = { Text("Repetições (padrão 10)") })
				}
			}
		}
		Button(onClick = {
			scope.launch {
				val sessionId = db.workoutDao().insertSession(WorkoutSession(epochDay = epochDay, type = "PPL"))
				val sets = inputs.mapNotNull { input ->
					val w = input.weightKg.toDoubleOrNull() ?: 0.0
					val r = input.reps.toIntOrNull() ?: 0
					if (w > 0.0 && r > 0) {
						ExerciseSet(
							sessionId = sessionId,
							exerciseName = input.name,
							weightKg = w,
							reps = r
						)
					} else null
				}
				if (sets.isNotEmpty()) {
					db.workoutDao().insertSets(sets)
				}
				onBack()
			}
		}) { Text("Salvar e voltar") }
	}
}

