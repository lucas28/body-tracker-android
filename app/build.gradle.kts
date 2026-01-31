plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	kotlin("kapt")
}

android {
	namespace = "com.bodyrecomptracker"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.bodyrecomptracker"
		minSdk = 24
		targetSdk = 34
		versionCode = 1
		versionName = "1.0.0"
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {
		release {
			isMinifyEnabled = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
		debug {
			isMinifyEnabled = false
		}
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
		isCoreLibraryDesugaringEnabled = true
	}
	kotlinOptions {
		jvmTarget = "17"
	}
	buildFeatures {
		compose = true
		buildConfig = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.9"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

dependencies {
	val composeBom = platform("androidx.compose:compose-bom:2024.10.00")
	implementation(composeBom)
	androidTestImplementation(composeBom)

	// Compose
	implementation("androidx.activity:activity-compose:1.9.0")
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-tooling-preview")
	debugImplementation("androidx.compose.ui:ui-tooling")
	implementation("androidx.compose.material3:material3:1.2.1")
	implementation("androidx.navigation:navigation-compose:2.7.7")
	// Icons para bottom navigation
	implementation("androidx.compose.material:material-icons-extended:1.7.5")
	// Material Components (themes XML - Theme.Material3.*)
	implementation("com.google.android.material:material:1.11.0")

	// Lifecycle
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
	implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

	// Room
	implementation("androidx.room:room-ktx:2.6.1")
	kapt("androidx.room:room-compiler:2.6.1")

	// WorkManager
	implementation("androidx.work:work-runtime-ktx:2.9.0")

	// Coroutines
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

	// Testing
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
	debugImplementation("androidx.compose.ui:ui-test-manifest")

	// Desugaring para java.time em MinSdk 24
	coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}

