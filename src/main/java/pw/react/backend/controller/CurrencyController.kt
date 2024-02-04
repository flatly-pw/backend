package pw.react.backend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pw.react.backend.utils.Currency
import pw.react.backend.web.CurrencyExchangeRatesDto

@RestController
@RequestMapping("/currency")
class CurrencyController {

    @Operation(
        summary = "Get exchange rates from USD to other currencies",
        description = "Price in target currency is equal to: `price_in_usd * exchange_rate_to_target_currency`. " +
                "Note: Every price returned from backend is USD. "
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully got exchange rates",
        content = [
            Content(mediaType = "application/json", schema = Schema(oneOf = [CurrencyExchangeRatesDto::class]))
        ]
    )
    @GetMapping("/exchangeRates")
    fun getExchangeRatesFromUsd(): ResponseEntity<CurrencyExchangeRatesDto> = ResponseEntity.ok(
        CurrencyExchangeRatesDto(
            usd = 1 / Currency.Unit.USD.toUsdExchangeRate,
            eur = 1 / Currency.Unit.EUR.toUsdExchangeRate,
            pln = 1 / Currency.Unit.PLN.toUsdExchangeRate
        )
    )
}
