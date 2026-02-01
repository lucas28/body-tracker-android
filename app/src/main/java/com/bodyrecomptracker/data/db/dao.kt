package com.bodyrecomptracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyProfileDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun upsert(profile: BodyProfile): Long

	@Query("SELECT * FROM BodyProfile ORDER BY id DESC LIMIT 1")
	fun observeLatest(): Flow<BodyProfile?>
}

@Dao
interface MealDao {
	@Insert
	suspend fun insert(meal: MealEntry): Long

	@Query("SELECT * FROM MealEntry WHERE epochDay = :epochDay")
	fun observeMealsForDay(epochDay: Long): Flow<List<MealEntry>>

	@Query("SELECT * FROM MealEntry WHERE epochDay BETWEEN :start AND :end")
	fun observeMealsBetween(start: Long, end: Long): Flow<List<MealEntry>>

	@Query("DELETE FROM MealEntry WHERE id = :id")
	suspend fun deleteById(id: Long)
}

@Dao
interface WorkoutDao {
	@Insert
	suspend fun insertSession(session: WorkoutSession): Long

	@Insert
	suspend fun insertSets(sets: List<ExerciseSet>)

	@Query("SELECT * FROM WorkoutSession WHERE epochDay = :epochDay")
	fun observeSessionsForDay(epochDay: Long): Flow<List<WorkoutSession>>

	@Query("SELECT * FROM WorkoutSession WHERE epochDay BETWEEN :start AND :end")
	fun observeSessionsBetween(start: Long, end: Long): Flow<List<WorkoutSession>>

	@Query("SELECT * FROM ExerciseSet WHERE sessionId = :sessionId")
	fun observeSets(sessionId: Long): Flow<List<ExerciseSet>>

	@Query("SELECT * FROM ExerciseSet WHERE exerciseName = :exerciseName ORDER BY id DESC LIMIT 1")
	suspend fun findLastSetForExercise(exerciseName: String): ExerciseSet?
}

