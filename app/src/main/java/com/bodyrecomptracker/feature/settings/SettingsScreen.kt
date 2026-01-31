package com.bodyrecomptracker.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(onBack: () -> Unit) {
	val proteinTarget = remember { mutableStateOf("150") }
	val deficitTarget = remember { mutableStateOf("500") }
	Column(
		modifier = Modifier
			.fillMaxSize()
			.statusBarsPadding()
			.navigationBarsPadding()
			.imePadding()
			.padding(16.dp)
			.verticalScroll(rememberScrollState()),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text("Configurações")
		OutlinedTextField(proteinTarget.value, { proteinTarget.value = it }, label = { Text("Proteína alvo (g)") })
		OutlinedTextField(deficitTarget.value, { deficitTarget.value = it }, label = { Text("Déficit alvo (kcal)") })
		Button(onClick = onBack) { Text("Salvar") }
	}
}

