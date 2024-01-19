package pw.react.backend.services

import pw.react.backend.dao.FlatPriceRepository

class FlatPriceService(private val flatPriceRepository: FlatPriceRepository) {

    fun getPriceByFlatId(flatId: String) = flatPriceRepository.getPriceEntityByFlatId(flatId).priceDollars
}
