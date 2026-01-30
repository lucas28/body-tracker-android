package com.bodyrecomptracker.notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bodyrecomptracker.App
import com.bodyrecomptracker.R
import com.bodyrecomptracker.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class ProteinReminderWorker(
	appContext: Context,
	params: WorkerParameters
) : CoroutineWorker(appContext, params) {
	override suspend fun doWork(): Result {
		return try {
			val db = AppDatabase.get(applicationContext)
			val today = LocalDate.now().toEpochDay()
			val meals = db.mealDao().observeMealsForDay(today).first()
			val totalProtein = meals.sumOf { it.proteinGrams }

			// Alvo padrão 150g; poderia vir de Settings no futuro
			if (totalProtein < 150.0) {
				notifyLowProtein(totalProtein)
			}
			Result.success()
		} catch (t: Throwable) {
			Result.retry()
		}
	}

	private fun notifyLowProtein(currentProtein: Double) {
		val notification = NotificationCompat.Builder(applicationContext, App.PROTEIN_CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_notification)
			.setContentTitle("Proteína abaixo da meta")
			.setContentText("Consumido ${currentProtein.toInt()}g. Tente bater 150g até o fim do dia.")
			.setAutoCancel(true)
			.build()
		NotificationManagerCompat.from(applicationContext).notify(1001, notification)
	}
}

