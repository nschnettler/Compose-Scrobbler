package de.schnettler.scrobbler.util

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import org.junit.Test
import kotlin.math.exp

class StringUtilTest {
    @Test
    fun `toFlagEmoji returns crossed flags when input is invalid`() {
        // WHEN - toFlagEmoji is run
        val countryCode = "de".toFlagEmoji()
        val unknownCountry = "unknown".toFlagEmoji()
        val emptyCountry = "".toFlagEmoji()

        // THEN - Result should be a placeholder flag
        expect(countryCode).toBe(GENERIC_FLAG_EMOJI)
        expect(unknownCountry).toBe(GENERIC_FLAG_EMOJI)
        expect(emptyCountry).toBe(GENERIC_FLAG_EMOJI)
    }

    @Test
    fun `toFlagEmoji returns correct flag when input is valid`() {
        // WHEN - toFlagEmoji is run
        val de = "Germany".toFlagEmoji()
        val usa = "United States".toFlagEmoji()
        val uk = "United Kingdom".toFlagEmoji()

        // THEN - Result should be the correct flag
        expect(de).toBe("\uD83C\uDDE9\uD83C\uDDEA")
        expect(usa).toBe("\uD83C\uDDFA\uD83C\uDDF8")
        expect(uk).toBe("\uD83C\uDDEC\uD83C\uDDE7")
    }

    @Test
    fun `toFlagEmoji returns correct flag when input is not capitalized`() {
        // WHEN - toFlagEmoji is run
        val de = "germany".toFlagEmoji()
        val usa = "united states".toFlagEmoji()
        val uk = "united Kingdom".toFlagEmoji()

        // THEN - Result should be the correct flag
        expect(de).toBe("\uD83C\uDDE9\uD83C\uDDEA")
        expect(usa).toBe("\uD83C\uDDFA\uD83C\uDDF8")
        expect(uk).toBe("\uD83C\uDDEC\uD83C\uDDE7")
    }


    @Test
    fun `capitalizeAll returns a string with all words capitalized`() {
        val simple = "word1".capitalizeAll()
        val multiple = "word1 word2".capitalizeAll()

        expect(simple).toBe("Word1")
        expect(multiple).toBe("Word1 Word2")
    }

    @Test
    fun `firstLetter returns the first letter of a string`() {
        val onlyLetters = "simple"
        val startsWithNumber = "5imple"


        expect(onlyLetters.firstLetter()).toBe("s")
        expect(startsWithNumber.firstLetter()).toBe("i")
    }

    @Test
    fun  `firstLetter throws exception on empty and number only strings`() {
        val blank = " "
        val onlyNumbers = "1960"

        expect {
            blank.firstLetter()
        }.toThrow<NoSuchElementException>()

        expect {
            onlyNumbers.firstLetter()
        }.toThrow<NoSuchElementException>()
    }
}