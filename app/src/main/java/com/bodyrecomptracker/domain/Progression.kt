package com.bodyrecomptracker.domain

import com.bodyrecomptracker.data.db.ExerciseSet

object Progression {
	fun copyLastSetForExercise(last: ExerciseSet?, defaultExerciseName: String): ExerciseSet {
		return if (last != null) {
			last.copy(id = 0, sessionId = 0) // pronto para inserir em nova sessão
		} else {
			ExerciseSet(
				id = 0,
				sessionId = 0,
				exerciseName = defaultExerciseName,
				weightKg = 0.0,
				reps = 0
			)
		}
	}
}

