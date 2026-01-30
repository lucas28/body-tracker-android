package com.bodyrecomptracker

import com.bodyrecomptracker.domain.Calculators
import com.bodyrecomptracker.domain.TmbInput
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorsTest {
	@Test
	fun testMifflinStJeorMale() {
		val input = TmbInput(weightKg = 90.0, heightCm = 168.0, ageYears = 28, isMale = true)
		val bmr = Calculators.calculateBmr(input)
		// Valor esperado aproximado
		assertEquals(10*90.0 + 6.25*168.0 - 5*28 + 5.0, bmr, 0.01)
	}

	@Test
	fun testProteinRange() {
		val (low, high) = Calculators.proteinTargetRange(90.0)
		assertEquals(144, low) // 1.6*90 => 144
		assertEquals(180, high) // 2.0*90 => 180
	}
}

