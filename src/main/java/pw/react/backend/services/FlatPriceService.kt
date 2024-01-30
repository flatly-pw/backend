package pw.react.backend.services

import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import pw.react.backend.dao.FlatPriceRepository

class FlatPriceService(private val flatPriceRepository: FlatPriceRepository) {

    fun getPriceByFlatId(flatId: String) = flatPriceRepository.getPriceEntityByFlatId(flatId).priceDollars

    fun getPriceByFlatId(flatId: String, start: LocalDate, end: LocalDate): Double {
        val pricePerNight = getPriceByFlatId(flatId)
        val nights = (end - start).days - 1
        return pricePerNight * nights
    }
}
