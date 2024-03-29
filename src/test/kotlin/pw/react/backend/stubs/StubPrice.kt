package pw.react.backend.stubs

import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.PriceEntity

fun stubPriceEntity(
    priceDollars: Double = 20.0,
    flat: FlatEntity = stubFlatEntity()
) = PriceEntity(priceDollars, flat)
