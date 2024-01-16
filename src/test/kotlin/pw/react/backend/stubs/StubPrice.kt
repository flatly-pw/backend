package pw.react.backend.stubs

import pw.react.backend.models.entity.PriceEntity

fun stubPriceEntity(
    id: Long? = 1L,
    priceDollars: Double = 20.0,
    priceEuros: Double = 20.0,
    pricePln: Double = 100.0,
) = PriceEntity(priceDollars, priceEuros, pricePln, id)
