package com.bodyrecomptracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.bodyrecomptracker.notifications.ProteinReminderScheduler

class App : Application() {
	override fun onCreate() {
		super.onCreate()
		createNotificationChannels()
		ProteinReminderScheduler.scheduleDailyProteinReminder(this)
	}

	private fun createNotificationChannels() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				PROTEIN_CHANNEL_ID,
				"Alertas de Proteína",
				NotificationManager.IMPORTANCE_DEFAULT
			).apply {
				description = "Notifica se a meta de proteína do dia não foi atingida"
			}
			val nm = getSystemService(NotificationManager::class.java)
			nm.createNotificationChannel(channel)
		}
	}

	companion object {
		const val PROTEIN_CHANNEL_ID = "protein_reminders"
	}
}

