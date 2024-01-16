package pw.react.backend.utils

@JvmInline
value class Currency private constructor(private val cents: Int) {

    sealed class Unit(val toUsdExchangeRate: Double) {
        data object USD : Unit(1.00)
        data object EUR : Unit(1.09)
        data object PLN : Unit(0.24)
    }

    val inUsd: Double get() = toDouble(Unit.USD)
    val inEuro: Double get() = toDouble(Unit.EUR)
    val inPln: Double get() = toDouble(Unit.PLN)

    fun Currency.plus(other: Currency) = Currency(cents + other.cents)
    fun Currency.times(factor: Int) = Currency(cents * factor)
    fun Currency.times(factor: Double) = Currency((cents * factor).toInt())

    private fun toDouble(targetCurrency: Unit) = cents.toDouble() / (targetCurrency.toUsdExchangeRate * 100)

    companion object {

        private fun toCurrency(value: Number, unit: Unit) = Currency(
            (value.toDouble() * 100.0 * unit.toUsdExchangeRate).toInt()
        )

        val Double.usd get() = toCurrency(this, Unit.USD)
        val Double.euro get() = toCurrency(this, Unit.EUR)
        val Double.pln get() = toCurrency(this, Unit.PLN)
        val Int.usd get() = toCurrency(this, Unit.USD)
        val Int.euro get() = toCurrency(this, Unit.EUR)
        val Int.pln get() = toCurrency(this, Unit.PLN)
    }

}
