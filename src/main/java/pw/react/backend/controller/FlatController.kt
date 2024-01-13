package pw.react.backend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
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

    @Operation(summary = "Get flat offers")
    @ApiResponse(
        responseCode = "200",
        description = "Successfully got flat list. data contains Flat",
        content = [
            Content(mediaType = "application/json", schema = Schema(oneOf = [PageDto::class]))
        ]
    )
    @GetMapping("/flats")
    fun getAllFlats(@RequestParam page: Int, @RequestParam pageSize: Int): ResponseEntity<*> = try {
        val flatPage = flatService.findAll(PageRequest.of(page, pageSize))
        ResponseEntity.ok(flatPage.toDto(Flat::toDto))
    } catch (e: IllegalArgumentException) {
        ResponseEntity.badRequest().body(e.message)
    }
}
