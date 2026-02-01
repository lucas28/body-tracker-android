package com.bodyrecomptracker.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bodyrecomptracker.data.db.AppDatabase
import com.bodyrecomptracker.data.db.AppSettings
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onBack: () -> Unit) {
	val ctx = LocalContext.current
	val scope = rememberCoroutineScope()
	val db = AppDatabase.get(ctx)
	val settings = db.appSettingsDao().observe().collectAsState(initial = null).value
	val proteinTarget = remember { mutableStateOf(settings?.proteinTargetGrams?.toString() ?: "150") }
	val deficitTarget = remember { mutableStateOf(settings?.deficitTargetKcal?.toString() ?: "500") }
	val useHC = remember { mutableStateOf(settings?.useHealthConnect ?: false) }
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
		Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
			Text("Usar Health Connect (quando disponível)")
			Switch(checked = useHC.value, onCheckedChange = { useHC.value = it }, colors = SwitchDefaults.colors())
		}
		Button(onClick = {
			scope.launch {
				val pt = proteinTarget.value.toIntOrNull() ?: 150
				val df = deficitTarget.value.toIntOrNull() ?: 500
				db.appSettingsDao().upsert(AppSettings(id = 1, proteinTargetGrams = pt, deficitTargetKcal = df, useHealthConnect = useHC.value))
				onBack()
			}
		}) { Text("Salvar") }
	}
}

