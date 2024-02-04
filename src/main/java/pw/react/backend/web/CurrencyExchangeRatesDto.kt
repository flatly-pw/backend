package pw.react.backend.web

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyExchangeRatesDto(
    val usd: Double,
    val eur: Double,
    val pln: Double,
)
