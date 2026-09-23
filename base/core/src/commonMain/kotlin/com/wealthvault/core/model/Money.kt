package com.wealthvault.core.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

/**
 * A currency-safe fixed-point monetary value.
 *
 * The wire layer may still receive JSON numbers for backwards compatibility,
 * but application code must never perform arithmetic with Double.
 */
@Serializable(with = MoneySerializer::class)
data class Money(
    val minorUnits: Long,
    val currencyCode: String = DEFAULT_CURRENCY,
) : Comparable<Money> {

    init {
        require(currencyCode.length == 3 && currencyCode.all { it in 'A'..'Z' }) {
            "currencyCode must be an uppercase ISO-4217 code"
        }
    }

    override fun compareTo(other: Money): Int {
        requireSameCurrency(other)
        return minorUnits.compareTo(other.minorUnits)
    }

    operator fun plus(other: Money): Money {
        requireSameCurrency(other)
        return copy(minorUnits = checkedAdd(minorUnits, other.minorUnits))
    }

    operator fun minus(other: Money): Money {
        requireSameCurrency(other)
        return copy(minorUnits = checkedSubtract(minorUnits, other.minorUnits))
    }

    /** Multiplies money by a fixed-scale quantity without using floating point. */
    operator fun times(quantity: FixedDecimal): Money {
        val product = checkedMultiply(minorUnits, quantity.unscaled)
        val divisor = FixedDecimal.scaleFactor(quantity.scale)
        val quotient = product / divisor
        val remainder = product % divisor
        val rounded = if (kotlin.math.abs(remainder) * 2L >= divisor) {
            quotient + if (product < 0) -1L else 1L
        } else {
            quotient
        }
        return copy(minorUnits = rounded)
    }

    fun decimalString(): String {
        if (minorUnits == Long.MIN_VALUE) return "-92233720368547758.08"
        val sign = if (minorUnits < 0) "-" else ""
        val absolute = kotlin.math.abs(minorUnits)
        val whole = absolute / MINOR_UNITS_PER_UNIT
        val fraction = (absolute % MINOR_UNITS_PER_UNIT).toString().padStart(2, '0')
        return "$sign$whole.$fraction"
    }

    /**
     * Converts to a major-unit number only at an integration boundary (for
     * example a legacy JSON DTO or a presentation formatter). Domain
     * arithmetic must continue to use [plus], [minus], and [compareTo].
     */
    fun toMajorUnits(): Double = decimalString().toDouble()

    private fun requireSameCurrency(other: Money) {
        require(currencyCode == other.currencyCode) {
            "Cannot operate on $currencyCode and ${other.currencyCode}"
        }
    }

    companion object {
        const val DEFAULT_CURRENCY = "THB"
        private const val MINOR_UNITS_PER_UNIT = 100L

        private fun checkedAdd(left: Long, right: Long): Long {
            val result = left + right
            require(!((right > 0 && result < left) || (right < 0 && result > left))) {
                "Money arithmetic overflow"
            }
            return result
        }

        private fun checkedSubtract(left: Long, right: Long): Long {
            val result = left - right
            require(!((right < 0 && result < left) || (right > 0 && result > left))) {
                "Money arithmetic overflow"
            }
            return result
        }

        private fun checkedMultiply(left: Long, right: Long): Long {
            if (left == 0L || right == 0L) return 0L
            require(!(left == Long.MIN_VALUE && right == -1L) &&
                !(right == Long.MIN_VALUE && left == -1L)) {
                "Money arithmetic overflow"
            }
            val result = left * right
            require(result / right == left) { "Money arithmetic overflow" }
            return result
        }

        fun fromDecimal(value: String?, currencyCode: String = DEFAULT_CURRENCY): Money? {
            if (value.isNullOrBlank()) return null
            val normalized = value.trim()
            if (normalized.drop(1).any { it == '+' || it == '-' }) return null
            val negative = normalized.startsWith('-')
            val unsigned = normalized.removePrefix("+").removePrefix("-")
            if (unsigned.isEmpty() || unsigned.none(Char::isDigit) || unsigned.count { it == '.' } > 1 ||
                unsigned.any { !it.isDigit() && it != '.' }
            ) return null
            val parts = unsigned.split('.', limit = 2)
            val whole = parts[0].ifBlank { "0" }.toLongOrNull() ?: return null
            val fractionText = parts.getOrNull(1).orEmpty()
            // The character validation above guarantees that this two-digit
            // slice is numeric; using a non-null conversion keeps an
            // unreachable nullable branch out of the money coverage contract.
            val fraction = fractionText.padEnd(2, '0').take(2).toLong()
            val rounded = if (fractionText.length > 2 && fractionText[2] >= '5') 1L else 0L
            val magnitudeFraction = fraction + rounded
            val maxWhole = Long.MAX_VALUE / MINOR_UNITS_PER_UNIT
            val maxFraction = Long.MAX_VALUE % MINOR_UNITS_PER_UNIT
            // Negative Long.MIN_VALUE has one extra representable magnitude
            // (..08 instead of ..07). Handle that exact boundary before
            // negating so the intermediate magnitude never wraps.
            val allowedFraction = if (negative) maxFraction + 1L else maxFraction
            if (whole > maxWhole || (whole == maxWhole && magnitudeFraction > allowedFraction)) {
                return null
            }
            val isNegativeLongMin = negative && whole == maxWhole && magnitudeFraction == allowedFraction
            val minor = if (isNegativeLongMin) {
                Long.MIN_VALUE
            } else {
                whole * MINOR_UNITS_PER_UNIT + magnitudeFraction
            }
            return Money(if (isNegativeLongMin) minor else if (negative) -minor else minor, currencyCode.uppercase())
        }

        fun fromDouble(value: Double?, currencyCode: String = DEFAULT_CURRENCY): Money? =
            value?.takeUnless { it.isNaN() || it.isInfinite() }?.let {
                fromDecimal(it.toString(), currencyCode)
            }
    }
}

/**
 * Reads both the current object representation and the numeric JSON shape
 * emitted by the pre-Money cache. New writes remain explicit and lossless.
 */
object MoneySerializer : KSerializer<Money> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Money") {
        element<Long>("minorUnits")
        element<String>("currencyCode")
    }

    override fun serialize(encoder: Encoder, value: Money) = encoder.encodeStructure(descriptor) {
        encodeLongElement(descriptor, 0, value.minorUnits)
        encodeStringElement(descriptor, 1, value.currencyCode)
    }

    override fun deserialize(decoder: Decoder): Money {
        val jsonDecoder = decoder as? JsonDecoder
        if (jsonDecoder != null) {
            return decodeJson(jsonDecoder.decodeJsonElement())
        }

        var minorUnits = 0L
        var currencyCode = Money.DEFAULT_CURRENCY
        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break
                    0 -> minorUnits = decodeLongElement(descriptor, index)
                    1 -> currencyCode = decodeStringElement(descriptor, index)
                    else -> throw SerializationException("Unknown Money field index: $index")
                }
            }
        }
        return Money(minorUnits, currencyCode)
    }

    private fun decodeJson(element: kotlinx.serialization.json.JsonElement): Money {
        if (element is JsonPrimitive && !element.isString) {
            return Money.fromDecimal(element.content)
                ?: throw SerializationException("Invalid legacy Money value: ${element.content}")
        }
        if (element is JsonPrimitive && element.isString && element.content.toDoubleOrNull() != null) {
            return Money.fromDecimal(element.content)
                ?: throw SerializationException("Invalid legacy Money value: ${element.content}")
        }
        val objectValue = element as? JsonObject
            ?: throw SerializationException("Expected Money object or numeric value")
        val minor = objectValue["minorUnits"]?.jsonPrimitive?.longOrNull
            ?: throw SerializationException("Money.minorUnits is missing")
        val currency = objectValue["currencyCode"]?.jsonPrimitive?.content
            ?: Money.DEFAULT_CURRENCY
        return Money(minor, currency)
    }
}

private fun Long.absoluteValueString(): String =
    if (this == Long.MIN_VALUE) "9223372036854775808" else kotlin.math.abs(this).toString()

/** Fixed-scale decimal for quantities, rates, and share counts. */
@Serializable
data class FixedDecimal(
    val unscaled: Long,
    val scale: Int,
) {
    init {
        require(scale in 0..9) { "scale must be between 0 and 9" }
    }

    fun decimalString(): String {
        if (scale == 0) return unscaled.toString()
        val negative = unscaled < 0
        val absolute = unscaled.absoluteValueString().padStart(scale + 1, '0')
        val splitAt = absolute.length - scale
        val sign = if (negative) "-" else ""
        return "$sign${absolute.substring(0, splitAt)}.${absolute.substring(splitAt)}"
    }

    companion object {
        internal fun scaleFactor(scale: Int): Long {
            var result = 1L
            repeat(scale) { result *= 10L }
            return result
        }

        fun fromDecimal(value: String?, scale: Int): FixedDecimal? {
            if (value.isNullOrBlank()) return null
            require(scale in 0..9) { "scale must be between 0 and 9" }
            val normalized = value.trim()
            if (normalized.drop(1).any { it == '+' || it == '-' }) return null
            val negative = normalized.startsWith('-')
            val unsigned = normalized.removePrefix("+").removePrefix("-")
            if (unsigned.isEmpty() || unsigned.none(Char::isDigit) || unsigned.count { it == '.' } > 1 ||
                unsigned.any { !it.isDigit() && it != '.' }
            ) return null
            val parts = unsigned.split('.', limit = 2)
            val whole = parts[0].ifBlank { "0" }.toLongOrNull() ?: return null
            val fractionText = parts.getOrNull(1).orEmpty()
            val fraction = fractionText.padEnd(scale, '0').take(scale)
                .ifBlank { "0" }.toLongOrNull() ?: return null
            val multiplier = scaleFactor(scale)
            if (whole > (Long.MAX_VALUE - fraction) / multiplier) return null
            val unscaled = whole * multiplier + fraction
            return FixedDecimal(if (negative) -unscaled else unscaled, scale)
        }

    }
}
