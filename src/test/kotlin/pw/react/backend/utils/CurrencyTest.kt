package pw.react.backend.utils

import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import pw.react.backend.utils.Currency.Companion.euro
import pw.react.backend.utils.Currency.Companion.pln
import pw.react.backend.utils.Currency.Companion.usd

class CurrencyTest {

    @Test
    fun `Should convert usd to euro properly`() {
        val usd1 = 20.0.usd
        val usd2 = 20.usd
        usd1.inEuro shouldBe (18.34 plusOrMinus 1e-2)
        usd2.inEuro shouldBe (18.34 plusOrMinus 1e-2)
    }

    @Test
    fun `Should convert usd to pln properly`() {
        val usd1 = 20.0.usd
        val usd2 = 20.usd
        usd1.inPln shouldBe (83.33 plusOrMinus 1e-2)
        usd2.inPln shouldBe (83.33 plusOrMinus 1e-2)
    }

    @Test
    fun `Should convert usd to usd properly`() {
        val usd1 = 20.0.usd
        val usd2 = 20.usd
        usd1.inUsd shouldBe (20.0 plusOrMinus 1e-2)
        usd2.inUsd shouldBe (20.0 plusOrMinus 1e-2)
    }

    @Test
    fun `Should convert euro to usd properly`() {
        val euro1 = 20.0.euro
        val euro2 = 20.euro
        euro1.inUsd shouldBe (21.8 plusOrMinus 1e-2)
        euro2.inUsd shouldBe (21.8 plusOrMinus 1e-2)
    }

    @Test
    fun `Should convert euro to euro properly`() {
        val euro1 = 20.0.euro
        val euro2 = 20.euro
        euro1.inEuro shouldBe (20.0 plusOrMinus 1e-2)
        euro2.inEuro shouldBe (20.0 plusOrMinus 1e-2)
    }

    @Test
    fun `Should convert euro to pln properly`() {
        val euro1 = 20.0.euro
        val euro2 = 20.euro
        euro1.inPln shouldBe (90.83 plusOrMinus 1e-2)
        euro2.inPln shouldBe (90.83 plusOrMinus 1e-2)
    }

    @Test
    fun `Should convert pln to usd properly`() {
        val pln1 = 20.0.pln
        val pln2 = 20.pln
        pln1.inUsd shouldBe (4.8 plusOrMinus 1e-2)
        pln2.inUsd shouldBe (4.8 plusOrMinus 1e-2)
    }

    @Test
    fun `Should convert pln to euro properly`() {
        val pln1 = 20.0.pln
        val pln2 = 20.pln
        pln1.inEuro shouldBe (4.4 plusOrMinus 1e-2)
        pln2.inEuro shouldBe (4.4 plusOrMinus 1e-2)
    }

    @Test
    fun `Should convert pln to pln properly`() {
        val pln1 = 20.0.pln
        val pln2 = 20.pln
        pln1.inPln shouldBe (20.0 plusOrMinus 1e-2)
        pln2.inPln shouldBe (20.0 plusOrMinus 1e-2)
    }
}
