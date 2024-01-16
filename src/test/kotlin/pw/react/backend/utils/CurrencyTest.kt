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
        val usd = 20.0.usd
        usd.inEuro shouldBe (18.34 plusOrMinus 1e-2)
    }

    @Test
    fun `Should convert usd to pln properly`() {
        val usd = 20.0.usd
        usd.inPln shouldBe (83.33 plusOrMinus 1e-2)
    }

    @Test
    fun `Should convert usd to usd properly`() {
        val usd = 20.0.usd
        usd.inUsd shouldBe (20.0 plusOrMinus 1e-2)
    }

    @Test
    fun `Should convert euro to usd properly`() {
        val euro = 20.0.euro
        euro.inUsd shouldBe (21.8 plusOrMinus 1e-2)
    }

    @Test
    fun `Should convert euro to euro properly`() {
        val euro = 20.0.euro
        euro.inEuro shouldBe (20.0 plusOrMinus 1e-2)
    }

    @Test
    fun `Should convert euro to pln properly`() {
        val euro = 20.0.euro
        euro.inPln shouldBe (90.83 plusOrMinus 1e-2)
    }

    @Test
    fun `Should convert pln to usd properly`() {
        val pln = 20.0.pln
        pln.inUsd shouldBe (4.8 plusOrMinus 1e-2)
    }

    @Test
    fun `Should convert pln to euro properly`() {
        val pln = 20.0.pln
        pln.inEuro shouldBe (4.4 plusOrMinus 1e-2)
    }

    @Test
    fun `Should convert pln to pln properly`() {
        val pln = 20.0.pln
        pln.inPln shouldBe (20.0 plusOrMinus 1e-2)
    }
}
