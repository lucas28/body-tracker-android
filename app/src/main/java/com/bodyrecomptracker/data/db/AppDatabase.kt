package com.bodyrecomptracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
	entities = [
		BodyProfile::class,
		MealEntry::class,
		WorkoutSession::class,
		ExerciseSet::class,
		AppSettings::class,
		WeightLog::class
	],
	version = 1,
	exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun bodyProfileDao(): BodyProfileDao
	abstract fun mealDao(): MealDao
	abstract fun workoutDao(): WorkoutDao

	companion object {
		@Volatile private var INSTANCE: AppDatabase? = null

		fun get(context: Context): AppDatabase =
			INSTANCE ?: synchronized(this) {
				INSTANCE ?: Room.databaseBuilder(
					context.applicationContext,
					AppDatabase::class.java,
					"body_recomp.db"
				).fallbackToDestructiveMigration().build().also { INSTANCE = it }
			}
	}
}

