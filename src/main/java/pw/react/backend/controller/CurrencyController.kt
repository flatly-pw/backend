package pw.react.backend.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pw.react.backend.utils.Currency
import pw.react.backend.web.CurrencyExchangeRatesDto

@RestController
@RequestMapping("/currency")
class CurrencyController {

    @GetMapping("/exchangeRates")
    fun getExchangeRatesFromUsd(): ResponseEntity<CurrencyExchangeRatesDto> = ResponseEntity.ok(
        CurrencyExchangeRatesDto(
            usd = 1 / Currency.Unit.USD.toUsdExchangeRate,
            eur = 1 / Currency.Unit.EUR.toUsdExchangeRate,
            pln = 1 / Currency.Unit.PLN.toUsdExchangeRate
        )
    )
}
