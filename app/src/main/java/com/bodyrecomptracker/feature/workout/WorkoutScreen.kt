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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import com.bodyrecomptracker.data.db.AppDatabase
import com.bodyrecomptracker.data.db.ExerciseSet
import com.bodyrecomptracker.data.db.WorkoutSession
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.bodyrecomptracker.ui.components.AppCard

data class ExerciseInput(val name: String, var weightKg: String = "", var reps: String = "10")

private data class Routine(val title: String, val exercises: List<String>)

private val routines: List<Routine> = listOf(
	Routine(
		title = "1 • Push",
		exercises = listOf(
			"Supino Inclinado (Halteres)",
			"Supino Reto (Barra/Máquina)",
			"Desenvolvimento Ombros (Halteres)",
			"Elevação Lateral",
			"Tríceps Pulley (Corda)"
		)
	),
	Routine(
		title = "2 • Pull",
		exercises = listOf(
			"Puxada Alta (Lat Pulldown)",
			"Remada Baixa Sentada (Cabo)",
			"Remada Curvada (Halteres)",
			"Encolhimento de Ombros",
			"Rosca Direta (Barra/Halteres)",
			"Rosca Martelo"
		)
	),
	Routine(
		title = "3 • Legs",
		exercises = listOf(
			"Leg Press 45°",
			"Cadeira Extensora",
			"Mesa/Cadeira Flexora",
			"Panturrilha no Leg Press/Sentado",
			"Abdominal Supra"
		)
	),
	Routine(
		title = "4 • Upper",
		exercises = listOf(
			"Supino Reto (Halteres)",
			"Puxada Triângulo (Pulley)",
			"Remada Articulada (Máquina)",
			"Desenvolvimento Arnold",
			"Tríceps Testa",
			"Rosca Alternada"
		)
	),
	Routine(
		title = "5 • Lower",
		exercises = listOf(
			"Agachamento Livre/Smith",
			"Afundo/Passada",
			"Stiff (Leve/Moderado)",
			"Panturrilha em Pé",
			"Prancha"
		)
	)
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun WorkoutScreen(onBack: () -> Unit) {
	var selectedRoutineIdx by remember { mutableStateOf(0) } // 0..4
	val inputs = remember { mutableStateListOf<ExerciseInput>() }
	val ctx = LocalContext.current
	val scope = rememberCoroutineScope()
	val db = AppDatabase.get(ctx)

	var currentDate by remember { mutableStateOf(LocalDate.now()) }

	LaunchedEffect(Unit) {
		// Inicializa com rotina 1
		inputs.clear()
		inputs.addAll(routines[selectedRoutineIdx].exercises.map { ExerciseInput(it) })
		// Preenche última carga conhecida
		routines[selectedRoutineIdx].exercises.forEachIndexed { index, name ->
			val last = db.workoutDao().findLastSetForExercise(name)
			if (last != null) {
				inputs[index].weightKg = if (last.weightKg > 0.0) last.weightKg.toString() else ""
				inputs[index].reps = if (last.reps > 0) last.reps.toString() else "10"
			}
		}
	}

	LaunchedEffect(selectedRoutineIdx) {
		// Atualiza lista ao trocar rotina
		inputs.clear()
		inputs.addAll(routines[selectedRoutineIdx].exercises.map { ExerciseInput(it) })
		routines[selectedRoutineIdx].exercises.forEachIndexed { index, name ->
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
			.imePadding()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text("Treino — escolha 1 a 5 (rotina) — ${currentDate}")
		Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
			(0..4).forEach { idx ->
				OutlinedButton(
					onClick = { selectedRoutineIdx = idx },
					modifier = Modifier.wrapContentWidth()
				) { Text("${idx + 1}") }
			}
		}
		Text(routines[selectedRoutineIdx].title)
		LazyColumn(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f),
			contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 96.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			items(inputs, key = { it.name }) { item ->
				AppCard {
					Text(item.name)
					// DropdownMenu padrão ancorado a um TextField clicável
					var expanded by remember { mutableStateOf(false) }
					val options = remember { (0..300 step 5).map { it.toString() } }
					Box {
						OutlinedTextField(
							readOnly = true,
							value = item.weightKg.ifBlank { "Selecionar carga (kg)" },
							onValueChange = {},
							label = { Text("Carga (kg)") },
							trailingIcon = {
								IconButton(onClick = { expanded = !expanded }) {
									androidx.compose.material3.Icon(
										imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
										contentDescription = null
									)
								}
							},
							modifier = Modifier
								.fillMaxWidth()
								.clickable { expanded = true }
						)
						DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
				val sessionId = db.workoutDao().insertSession(
					WorkoutSession(epochDay = currentDate.toEpochDay(), type = routines[selectedRoutineIdx].title)
				)
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
		}, modifier = Modifier.fillMaxWidth()) { Text("Salvar Treino") }
	}
}

