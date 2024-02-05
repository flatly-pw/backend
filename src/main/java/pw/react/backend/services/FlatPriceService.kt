package pw.react.backend.services

import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import pw.react.backend.dao.FlatPriceRepository
import pw.react.backend.models.entity.FlatEntity
import pw.react.backend.models.entity.PriceEntity

class FlatPriceService(
    private val flatPriceRepository: FlatPriceRepository
    ) {

    fun getPriceByFlatId(flatId: String) = flatPriceRepository.getPriceEntityByFlatId(flatId).priceDollars

    fun getPriceByFlatId(flatId: String, start: LocalDate, end: LocalDate): Double {
        val pricePerNight = getPriceByFlatId(flatId)
        val nights = (end - start).days
        return pricePerNight * nights
    }

    fun savePriceByFlat(flat: FlatEntity, price: Double): PriceEntity {
        return flatPriceRepository.save(
            PriceEntity(
                priceDollars = price,
                flat = flat,
            )
        )
    }

    fun updatePriceByFlatId(flatId: String, pricePerNight: Double) {
        val oldpriceEntity = flatPriceRepository.getPriceEntityByFlatId(flatId)
        val flatEntity = oldpriceEntity.flat
        flatPriceRepository.delete(oldpriceEntity)
        flatPriceRepository.save(
            PriceEntity(
                priceDollars = pricePerNight,
                flat = flatEntity
            )
        )

    }
}
