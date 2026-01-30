package com.bodyrecomptracker.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.bodyrecomptracker.data.db.AppDatabase
import com.bodyrecomptracker.data.db.BodyProfile
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
	var weightKg by remember { mutableStateOf("90") }
	var heightCm by remember { mutableStateOf("168") }
	var age by remember { mutableStateOf("28") }
	var isMale by remember { mutableStateOf(true) }
	val ctx = LocalContext.current
	val scope = rememberCoroutineScope()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.Center
	) {
		Text(text = "Perfil Antropométrico")
		Spacer(Modifier.height(8.dp))
		OutlinedTextField(value = weightKg, onValueChange = { weightKg = it }, label = { Text("Peso (kg)") })
		OutlinedTextField(value = heightCm, onValueChange = { heightCm = it }, label = { Text("Altura (cm)") })
		OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Idade") })
		Spacer(Modifier.height(12.dp))
		Button(onClick = {
			scope.launch {
				val w = weightKg.toDoubleOrNull() ?: 0.0
				val h = heightCm.toDoubleOrNull() ?: 0.0
				val a = age.toIntOrNull() ?: 0
				AppDatabase.get(ctx).bodyProfileDao().upsert(
					BodyProfile(weightKg = w, heightCm = h, ageYears = a, isMale = isMale)
				)
				onFinished()
			}
		}) {
			Text("Continuar")
		}
	}
}

