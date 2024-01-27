package pw.react.backend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pw.react.backend.exceptions.FlatImageException
import pw.react.backend.exceptions.FlatNotFoundException
import pw.react.backend.models.FlatQueryFactory
import pw.react.backend.models.domain.Flat
import pw.react.backend.services.FlatDetailsService
import pw.react.backend.services.FlatImageService
import pw.react.backend.services.FlatService
import pw.react.backend.web.FlatDetailsDto
import pw.react.backend.web.PageDto
import pw.react.backend.web.toDto

@RestController
class FlatController(
    private val flatService: FlatService,
    private val flatDetailsService: FlatDetailsService,
    private val flatImageService: FlatImageService,
    private val flatQueryFactory: FlatQueryFactory,
) {

    @Operation(
        summary = "Get flat offers",
        description = "startDate and endDate are in yyyy-mm-dd format. Either both startDate and endDate needs to be provided or none."
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
        ResponseEntity.ok(flatPage.toDto(Flat::toDto))
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
    @ApiResponse(
        responseCode = "404",
        description = "Flat with given id was not found or thumbnail for this flat was not present",
    )
    @GetMapping("/flats/{flatId}")
    fun getFlatDetails(@PathVariable flatId: String): ResponseEntity<*> = try {
        ResponseEntity.ok(flatDetailsService.getFlatDetailsById(flatId).toDto())
    } catch (e: FlatNotFoundException) {
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
    } catch (e: FlatImageException) {
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
    }

    @Operation(summary = "Get flat image")
    @ApiResponse(
        responseCode = "200",
        description = "Successfully got flat image",
        content = [
            Content(mediaType = "image/*", schema = Schema(oneOf = [ByteArrayResource::class], format = "binary"))
        ]
    )
    @ApiResponse(
        responseCode = "404",
        description = "Image with given id for given flat id was not found."
    )
    @GetMapping("/flats/{flatId}/image/{imageId}")
    fun getFlatImage(@PathVariable flatId: String, @PathVariable imageId: String): ResponseEntity<*> = try {
        val image = flatImageService.getImage(imageId, flatId)
        ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(image.type)) //image.type should be in format: image/<type>, where type is png, jpg etc
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${image.name}\"")
            .body(ByteArrayResource(image.bytes))
    } catch (e: FlatImageException.ImageNotFound) {
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
    }
}
