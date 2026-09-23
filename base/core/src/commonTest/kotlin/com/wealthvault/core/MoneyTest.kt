package com.wealthvault.core

import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.core.model.Money
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class MoneyTest {
    @Test
    fun parsesAndFormatsFixedPointValues() {
        val money = Money.fromDecimal("1234.567")

        assertEquals(123457L, money?.minorUnits)
        assertEquals("1234.57", money?.decimalString())
    }

    @Test
    fun majorUnitConversionIsExplicitAndFixedToTwoDecimals() {
        assertEquals(123.46, Money.fromDecimal("123.456")!!.toMajorUnits())
    }

    @Test
    fun serializerReadsLegacyNumbersAndWritesExplicitObjects() {
        val json = Json
        assertEquals(12345, json.decodeFromString<Money>("123.45").minorUnits)

        val encoded = json.encodeToString(Money(12345))
        assertEquals(12345, json.decodeFromString<Money>(encoded).minorUnits)
    }

    @Test
    fun rejectsArithmeticAcrossCurrencies() {
        val thb = Money(100)
        val usd = Money(100, "USD")

        assertFailsWith<IllegalArgumentException> { thb + usd }
    }

    @Test
    fun parsesFixedScaleQuantities() {
        val quantity = FixedDecimal.fromDecimal("12.3456", scale = 4)

        assertEquals(123456L, quantity?.unscaled)
        assertEquals("12.3456", quantity?.decimalString())
    }

    @Test
    fun multipliesMoneyByFixedScaleQuantityWithoutFloatingPoint() {
        val total = Money(10_000) * FixedDecimal.fromDecimal("2.5", scale = 1)!!

        assertEquals(Money(25_000), total)
    }

    @Test
    fun roundsFixedScaleMultiplicationHalfUpForBothSigns() {
        assertEquals(Money(1), Money(100) * FixedDecimal.fromDecimal("0.005", scale = 3)!!)
        assertEquals(Money(-1), Money(-100) * FixedDecimal.fromDecimal("0.005", scale = 3)!!)
    }

    @Test
    fun invalidInputReturnsNull() {
        assertNull(Money.fromDecimal("not-a-number"))
        assertNull(FixedDecimal.fromDecimal("12.x", scale = 2))
    }

    @Test
    fun rejectsOverflowInsteadOfWrapping() {
        assertFailsWith<IllegalArgumentException> {
            Money(Long.MAX_VALUE) + Money(1)
        }
        assertFailsWith<IllegalArgumentException> {
            Money(Long.MIN_VALUE) + Money(-1)
        }
        assertEquals(Money(Long.MIN_VALUE), Money(Long.MIN_VALUE) + Money(0))
        assertEquals(Money(Long.MAX_VALUE), Money(Long.MAX_VALUE) + Money(0))
        assertNull(Money.fromDecimal("92233720368547758.08"))
        assertEquals(Money(Long.MIN_VALUE), Money.fromDecimal("-92233720368547758.08"))
        assertNull(Money.fromDecimal("-92233720368547758.09"))
        assertNull(FixedDecimal.fromDecimal("922337203685477580.8", scale = 1))
    }

    @Test
    fun formatsLongMinValueWithoutOverflowingItsMagnitude() {
        assertEquals("-92233720368547758.08", Money(Long.MIN_VALUE).decimalString())
        assertEquals("0.00", Money(0).decimalString())
        assertEquals(
            "-9223372036854775.808",
            FixedDecimal(Long.MIN_VALUE, scale = 3).decimalString(),
        )
    }

    @Test
    fun rejectsMalformedSignsAndCurrencyCodes() {
        assertNull(Money.fromDecimal("-"))
        assertNull(Money.fromDecimal("+-1"))
        assertNull(Money.fromDecimal("."))
        assertNull(FixedDecimal.fromDecimal("-+1", scale = 2))
        assertNull(FixedDecimal.fromDecimal(".", scale = 2))
        assertNull(Money.fromDecimal("1.2.3"))
        assertFailsWith<IllegalArgumentException> { Money(1, "thb") }
    }

    @Test
    fun supportsComparisonSubtractionAndSafeDoubleBoundary() {
        val thb = Money(500)
        assertEquals(Money(300), thb - Money(200))
        assertEquals(Money(700), thb - Money(-200))
        assertEquals(Money(499), thb + Money(-1))
        assertTrue(thb > Money(200))
        assertEquals(Money(12346, "USD"), Money.fromDouble(123.456, "usd"))
        assertNull(Money.fromDouble(Double.NaN))
        assertNull(Money.fromDouble(Double.POSITIVE_INFINITY))
        assertNull(Money.fromDouble(null))
    }

    @Test
    fun rejectsSubtractionAndMultiplicationOverflow() {
        assertFailsWith<IllegalArgumentException> { Money(Long.MIN_VALUE) - Money(1) }
        assertFailsWith<IllegalArgumentException> { Money(Long.MAX_VALUE) - Money(-1) }
        assertFailsWith<IllegalArgumentException> {
            Money(Long.MAX_VALUE) * FixedDecimal(2, 0)
        }
        assertFailsWith<IllegalArgumentException> {
            Money(Long.MAX_VALUE / 2 + 1) * FixedDecimal(2, 0)
        }
        assertFailsWith<IllegalArgumentException> {
            Money(Long.MIN_VALUE) * FixedDecimal(-1, 0)
        }
        assertEquals(Money(1), Money(-1) * FixedDecimal(-1, 0))
        assertFailsWith<IllegalArgumentException> {
            Money(-1) * FixedDecimal(Long.MIN_VALUE, 0)
        }
        assertEquals(Money(0), Money(0) * FixedDecimal(123, 2))
        assertEquals(Money(0), Money(123) * FixedDecimal(0, 2))
    }

    @Test
    fun serializerAcceptsNumericStringsAndObjectDefaults() {
        val json = Json
        assertEquals(Money(12345), json.decodeFromString<Money>("\"123.45\""))
        assertEquals(Money(12345), json.decodeFromString<Money>("{\"minorUnits\":12345}"))
        assertFailsWith<SerializationException> {
            json.decodeFromString<Money>("{\"currencyCode\":\"THB\"}")
        }
        assertFailsWith<SerializationException> {
            json.decodeFromString<Money>("true")
        }
        assertFailsWith<SerializationException> {
            json.decodeFromString<Money>("\"92233720368547758.08\"")
        }
        assertEquals(
            Money(Long.MIN_VALUE),
            json.decodeFromString<Money>("\"-92233720368547758.08\""),
        )
    }

    @Test
    fun fixedDecimalCoversZeroScaleAndValidationBoundaries() {
        assertEquals("12", FixedDecimal(12, 0).decimalString())
        assertEquals("-0.12", FixedDecimal(-12, 2).decimalString())
        assertFailsWith<IllegalArgumentException> { FixedDecimal(1, -1) }
        assertFailsWith<IllegalArgumentException> { FixedDecimal(1, 10) }
        assertEquals(FixedDecimal(12, 2), FixedDecimal.fromDecimal(".12", 2))
        assertEquals(FixedDecimal(1200, 2), FixedDecimal.fromDecimal("12.", 2))
        assertNull(FixedDecimal.fromDecimal(null, 2))
        assertNull(FixedDecimal.fromDecimal("+", 2))
        assertNull(FixedDecimal.fromDecimal("1.2.3", 2))
        assertEquals(FixedDecimal(-120, 2), FixedDecimal.fromDecimal("-1.2", 2))
        assertFailsWith<IllegalArgumentException> { FixedDecimal.fromDecimal("1", 10) }
    }

    @Test
    fun parserCoversSignsDigitsAndRoundingBoundaries() {
        assertEquals(Money(12345), Money.fromDecimal("+123.45"))
        assertEquals(Money(12345), Money.fromDecimal("123.454"))
        assertEquals(Money(12346), Money.fromDecimal("123.455"))
        assertNull(Money.fromDecimal("  "))
        assertNull(Money.fromDecimal(null))
        assertNull(Money.fromDecimal("1e3"))
        assertNull(Money.fromDecimal("1-2"))
        assertNull(Money.fromDecimal("abc"))
        assertNull(Money.fromDecimal("+"))
        assertEquals(Money(50), Money.fromDecimal(".5"))
        assertEquals(Money(100), Money.fromDecimal("1."))
        assertEquals(Money(-50), Money.fromDecimal("-.5"))
        assertNull(Money.fromDecimal("922337203685477580700"))
        assertFailsWith<IllegalArgumentException> { Money(1, "TH") }
        assertFailsWith<IllegalArgumentException> { Money(1, "TH1") }
        assertEquals(Money(0), Money.fromDecimal("000.00"))
        assertEquals(Money(100, "USD"), Money.fromDecimal("1", "usd"))
        assertEquals(Money(1, "THB"), Money(1, "THB"))
        assertFailsWith<IllegalArgumentException> { Money(1, "") }
    }
}
