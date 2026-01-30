package com.bodyrecomptracker.domain

import kotlin.math.roundToInt

data class TmbInput(
	val weightKg: Double,
	val heightCm: Double,
	val ageYears: Int,
	val isMale: Boolean
)

object Calculators {
	// Mifflin-St Jeor
	fun calculateBmr(input: TmbInput): Double {
		val s = if (input.isMale) 5.0 else -161.0
		return (10.0 * input.weightKg) + (6.25 * input.heightCm) - (5.0 * input.ageYears) + s
	}

	// GET simplificado por fator de atividade (ex.: 1.2 sedentário)
	fun calculateTdee(bmr: Double, activityFactor: Double = 1.2): Double {
		return bmr * activityFactor
	}

	// Meta de calorias com déficit
	fun targetCaloriesForDeficit(tdee: Double, deficit: Int = 500): Int {
		return (tdee - deficit).roundToInt().coerceAtLeast(1200) // simples guarda
	}

	// Proteína alvo (1.6 a 2.0 g/kg)
	fun proteinTargetRange(weightKg: Double): Pair<Int, Int> {
		val low = (weightKg * 1.6).roundToInt()
		val high = (weightKg * 2.0).roundToInt()
		return low to high
	}
}

