package pw.react.backend.stubs

import pw.react.backend.models.entity.PriceEntity

fun stubPriceEntity(
    id: Long? = 1L,
    priceDollars: Double = 20.0,
) = PriceEntity(priceDollars, id)
