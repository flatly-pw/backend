package pw.react.backend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pw.react.backend.models.FlatQueryFactory
import pw.react.backend.services.FlatService
import pw.react.backend.web.FlatDetailsDto
import pw.react.backend.web.FlatDto
import pw.react.backend.web.PageDto
import pw.react.backend.web.toDto

@RestController
class FlatController(private val flatService: FlatService, private val flatQueryFactory: FlatQueryFactory) {

    @Operation(
        summary = "Get flat offers",
        description = "startDate and endDate are in yyyy-mm-dd format. Either both startDate and endDate needs to be provided or none. " +
                "IMPORTANT: city, country, startDate, endDate, pets parameters are validated but ignored for now."
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
        val flatQuery = flatQueryFactory.create(
            page = page,
            pageSize = pageSize,
            city = city,
            country = country,
            startDateIso = startDate,
            endDateIso = endDate,
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
