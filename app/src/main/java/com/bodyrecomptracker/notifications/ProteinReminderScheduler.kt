package com.bodyrecomptracker.notifications

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime

object ProteinReminderScheduler {
	private const val UNIQUE_WORK_NAME = "protein_reminder_daily"

	fun scheduleDailyProteinReminder(context: Context) {
		// Agenda próxima execução às ~19:00 hoje (ou amanhã se já passou)
		val now = LocalDateTime.now()
		var next = now.withHour(19).withMinute(0).withSecond(0).withNano(0)
		if (next.isBefore(now)) {
			next = next.plusDays(1)
		}
		val delay = Duration.between(now, next)

		val request = OneTimeWorkRequestBuilder<ProteinReminderWorker>()
			.setInitialDelay(delay)
			.setConstraints(
				Constraints.Builder()
					// sem requisitos pesados; WorkManager não garante exatidão, mas é suficiente
					.build()
			)
			.build()

		WorkManager.getInstance(context)
			.enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
	}
}

