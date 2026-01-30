package com.bodyrecomptracker.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bodyrecomptracker.App
import com.bodyrecomptracker.R

class RestTimerWidget : AppWidgetProvider() {
	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
		for (appWidgetId in appWidgetIds) {
			updateWidget(context, appWidgetManager, appWidgetId, idle = true)
		}
	}

	override fun onReceive(context: Context, intent: Intent) {
		super.onReceive(context, intent)
		when (intent.action) {
			ACTION_START_60 -> startTimer(context, 60_000L)
			ACTION_START_90 -> startTimer(context, 90_000L)
			ACTION_TIMER_DONE -> notifyDone(context)
		}
	}

	private fun startTimer(context: Context, durationMs: Long) {
		val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		val pi = PendingIntent.getBroadcast(
			context,
			REQUEST_CODE_TIMER_DONE,
			Intent(context, RestTimerWidget::class.java).setAction(ACTION_TIMER_DONE),
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)
		am.setExactAndAllowWhileIdle(
			AlarmManager.ELAPSED_REALTIME_WAKEUP,
			SystemClock.elapsedRealtime() + durationMs,
			pi
		)
		// Atualiza todos widgets para estado "descansando..."
		val mgr = AppWidgetManager.getInstance(context)
		val cn = android.content.ComponentName(context, RestTimerWidget::class.java)
		val ids = mgr.getAppWidgetIds(cn)
		for (id in ids) {
			updateWidget(context, mgr, id, idle = false)
		}
	}

	private fun notifyDone(context: Context) {
		val notification = NotificationCompat.Builder(context, App.PROTEIN_CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_notification)
			.setContentTitle("Descanso finalizado")
			.setContentText("Volte para a próxima série.")
			.setAutoCancel(true)
			.build()
		NotificationManagerCompat.from(context).notify(2001, notification)

		// Volta ao estado ocioso
		val mgr = AppWidgetManager.getInstance(context)
		val cn = android.content.ComponentName(context, RestTimerWidget::class.java)
		val ids = mgr.getAppWidgetIds(cn)
		for (id in ids) {
			updateWidget(context, mgr, id, idle = true)
		}
	}

	private fun updateWidget(context: Context, manager: AppWidgetManager, appWidgetId: Int, idle: Boolean) {
		val views = RemoteViews(context.packageName, R.layout.widget_rest_timer)
		views.setTextViewText(
			R.id.txtStatus,
			if (idle) "Cronômetro de descanso" else "Descansando..."
		)
		val p60 = PendingIntent.getBroadcast(
			context,
			REQUEST_CODE_START_60,
			Intent(context, RestTimerWidget::class.java).setAction(ACTION_START_60),
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)
		val p90 = PendingIntent.getBroadcast(
			context,
			REQUEST_CODE_START_90,
			Intent(context, RestTimerWidget::class.java).setAction(ACTION_START_90),
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)
		views.setOnClickPendingIntent(R.id.btn60, p60)
		views.setOnClickPendingIntent(R.id.btn90, p90)

		manager.updateAppWidget(appWidgetId, views)
	}

	companion object {
		private const val ACTION_START_60 = "com.bodyrecomptracker.widget.ACTION_START_60"
		private const val ACTION_START_90 = "com.bodyrecomptracker.widget.ACTION_START_90"
		private const val ACTION_TIMER_DONE = "com.bodyrecomptracker.widget.ACTION_TIMER_DONE"
		private const val REQUEST_CODE_START_60 = 6001
		private const val REQUEST_CODE_START_90 = 6002
		private const val REQUEST_CODE_TIMER_DONE = 6003
	}
}

