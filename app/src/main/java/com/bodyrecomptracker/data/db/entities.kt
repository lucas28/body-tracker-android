package com.bodyrecomptracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class BodyProfile(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val createdAtEpochDay: Long = LocalDate.now().toEpochDay(),
	val weightKg: Double,
	val heightCm: Double,
	val ageYears: Int,
	val isMale: Boolean
)

@Entity
data class MealEntry(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val epochDay: Long, // por dia
	val name: String,
	val calories: Int,
	val proteinGrams: Double,
	val carbsGrams: Double,
	val fatGrams: Double
)

@Entity
data class WorkoutSession(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val epochDay: Long,
	val type: String // PPL / Upper / Lower
)

@Entity
data class ExerciseSet(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val sessionId: Long,
	val exerciseName: String,
	val weightKg: Double,
	val reps: Int
)

@Entity
data class AppSettings(
	@PrimaryKey val id: Long = 1,
	val proteinTargetGrams: Int = 150,
	val deficitTargetKcal: Int = 500,
	val useHealthConnect: Boolean = false
)

@Entity
data class WeightLog(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val epochDay: Long,
	val weightKg: Double
)
