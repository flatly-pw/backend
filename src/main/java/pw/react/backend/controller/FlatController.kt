package pw.react.backend.controller

import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pw.react.backend.models.domain.Flat
import pw.react.backend.services.FlatService
import pw.react.backend.web.PageDto
import pw.react.backend.web.toDto

@RestController
class FlatController(private val flatService: FlatService) {

    @GetMapping("/flats")
    fun getAllFlats(@RequestParam page: Int, @RequestParam pageSize: Int): ResponseEntity<PageDto<List<Flat>>> {
        val flatPage = flatService.findAll(PageRequest.of(page, pageSize))
        return ResponseEntity.ok(flatPage.toDto())
    }
}
