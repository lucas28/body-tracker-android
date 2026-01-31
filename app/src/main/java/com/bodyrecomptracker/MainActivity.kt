package com.bodyrecomptracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import com.bodyrecomptracker.navigation.AppNavHost
import com.bodyrecomptracker.navigation.AppScaffold
import com.bodyrecomptracker.ui.theme.BodyRecompTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		WindowCompat.setDecorFitsSystemWindows(window, false)
		requestNotificationPermissionIfNeeded()
		setContent {
			BodyRecompTheme {
				Surface {
					AppScaffold()
				}
			}
		}
	}

	private fun requestNotificationPermissionIfNeeded() {
		if (Build.VERSION.SDK_INT >= 33) {
			val has = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
			if (!has) {
				ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1010)
			}
		}
	}
}

