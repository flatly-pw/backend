package pw.react.backend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import kotlinx.datetime.LocalDate
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pw.react.backend.models.domain.FlatQuery
import pw.react.backend.services.FlatService
import pw.react.backend.web.FlatDetailsDto
import pw.react.backend.web.FlatDto
import pw.react.backend.web.PageDto
import pw.react.backend.web.toDto

@RestController
class FlatController(private val flatService: FlatService) {

    @Operation(
        summary = "Get flat offers",
        description = "FlatQueryDto is optional and has to have correct data. " +
                "\n1. start_date and end_date are in yyyy-mm-dd format." +
                "\n2. start_date, end_date, adults, children and pets are mandatory fields. " +
                "\n3. Every field in destination is optional."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully got flat list. data contains Flat",
        content = [
            Content(mediaType = "application/json", schema = Schema(oneOf = [PageDto::class]))
        ]
    )
    @ApiResponse(
        responseCode = "400",
        description = "FlatQueryDto contained invalid data.",
    )
    @GetMapping("/flats")
    fun getAllFlats(
        @RequestParam page: Int,
        @RequestParam pageSize: Int,
        @RequestParam city: String?,
        @RequestParam country: String?,
        @RequestParam startDate: String?,
        @RequestParam endDate: String?,
        @RequestParam beds: Int?,
        @RequestParam bedrooms: Int?,
        @RequestParam bathrooms: Int?,
        @RequestParam adults: Int?,
        @RequestParam children: Int?,
        @RequestParam pets: Int?,
    ): ResponseEntity<*> = try {
        val flatQuery = FlatQuery(
            page = page,
            pageSize = pageSize,
            city = city?.lowercase()?.trim(),
            country = country?.lowercase()?.trim(),
            startDate = startDate?.let(LocalDate::parse),
            endDate = endDate?.let(LocalDate::parse),
            beds = beds,
            bedrooms = bedrooms,
            bathrooms = bathrooms,
            adults = adults,
            children = children,
            pets = pets
        )
        val flatPage = flatService.findAll(flatQuery)
        ResponseEntity.ok(flatPage.toDto { FlatDto(id = it.id!!, title = it.title) })
    } catch (e: IllegalArgumentException) {
        ResponseEntity.badRequest().body(e.message)
    }

    @Operation(summary = "Get flat offer details")
    @ApiResponse(
        responseCode = "200",
        description = "Successfully got flat details.",
        content = [
            Content(mediaType = "application/json", schema = Schema(oneOf = [FlatDetailsDto::class]))
        ]
    )
    @GetMapping("/flats/{flatId}")
    fun getFlatDetails(@PathVariable flatId: String): ResponseEntity<*> = ResponseEntity.ok(FlatDetailsDto())
}
