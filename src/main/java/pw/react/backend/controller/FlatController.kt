package pw.react.backend.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.hibernate.action.spi.Executable
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import pw.react.backend.dao.FlatOwnerRepository
import pw.react.backend.exceptions.FlatImageException
import pw.react.backend.exceptions.FlatNotFoundException
import pw.react.backend.exceptions.FlatValidationException
import pw.react.backend.exceptions.InvalidFileException
import pw.react.backend.models.FlatQueryFactory
import pw.react.backend.models.domain.Address
import pw.react.backend.models.domain.Flat
import pw.react.backend.models.domain.FlatOwner
import pw.react.backend.models.domain.toDomain
import pw.react.backend.services.*
import pw.react.backend.utils.TimeProvider
import pw.react.backend.web.*


@RestController
class FlatController(
    private val flatService: FlatService,
    private val flatDetailsService: FlatDetailsService,
    private val flatImageService: FlatImageService,
    private val flatQueryFactory: FlatQueryFactory,
    private val flatOwnerService: FlatOwnerService,
    private val timeProvider: TimeProvider,
    private val flatOwnerRepository: FlatOwnerRepository,
    private val addressService: AddressService,
    private val flatPriceService: FlatPriceService,
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


    @Operation(summary = "Create new flat")
    @ApiResponse(
    responseCode = "201",
    description = "Succesfully created a flat",
    content = [
    Content(mediaType = "application/json", schema = Schema(oneOf = [FlatDetailsDto::class]))
    ]
    )
    @ApiResponse(
    responseCode = "401",
    description = "Something went wrong"
    )
    @PostMapping("/admin/flats")
    fun postFlat(@RequestBody newFlatDto: NewFlatDto): ResponseEntity<*> =
    try {
        val email = newFlatDto.flatowneremail
        val flatOwnerEntity = flatOwnerRepository.findByEmail(email)
        var flatOwner: FlatOwner
        if (flatOwnerEntity == null) {
            flatOwner = FlatOwner(
                name = newFlatDto.flatownername,
                lastName = newFlatDto.flatownerlastName,
                email = email,
                phoneNumber = newFlatDto.flatownerphoneNumber,
                registeredAt = timeProvider().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            )
            //val newFlatOwner =  flatOwnerService.save(flatOwner)
            //flatOwner = newFlatOwner
        }
        else{
            flatOwner = flatOwnerEntity.toDomain()
        }


        val address = Address(
            street = newFlatDto.street,
            postalCode = newFlatDto.postalCode,
            city = newFlatDto.city,
            country = newFlatDto.country,
            latitude = newFlatDto.latitude,
            longitude = newFlatDto.longitude
        )
        //val newAddress = addressService.save(address)

        val newFlat = newFlatDto.toDomain(address,flatOwner, timeProvider().toLocalDateTime(TimeZone.currentSystemDefault()).date.toJavaLocalDate())
        val savedFlat = flatService.saveNewFlat(newFlat)

        val price = flatPriceService.savePriceByFlat(savedFlat, newFlatDto.pricePerNight)

        //save facils

        ResponseEntity.ok(savedFlat.id)

    } catch (ex: Exception) {
    throw FlatValidationException(ex.message, UserController.USERS_PATH)
    }

    @Operation(summary = "Add an image")
    @ApiResponse(
        responseCode = "201",
        description = "Succesfully added an image",
        content = [
            Content(mediaType = "application/json", schema = Schema(oneOf = [FlatDetailsDto::class]))
        ]
    )
    @ApiResponse(
        responseCode = "401",
        description = "Something went wrong"
    )
    @PostMapping("/admin/flats/{flatId}/image")
    fun postImage(@PathVariable flatId: String,
                  @RequestParam("file") file: MultipartFile
    ): ResponseEntity<*> = try {

        val image = flatImageService.saveImage(flatId, file)

        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/flats/$flatId/image/")
            .path(image.id)
            .toUriString()
        val response = UploadFileResponse(
            image.name,
            fileDownloadUri,
            file.contentType,
            file.size
        )
        ResponseEntity.ok(response)
    }
    catch (e: InvalidFileException) {
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
    }
    catch (e: Exception){
        throw FlatValidationException(e.message, UserController.USERS_PATH)
    }

    @Operation(summary = "Update existing flat")
    @ApiResponse(
        responseCode = "200",
        description = "Succesfully updated a flat",
    )
    @ApiResponse(
        responseCode = "401",
        description = "Something went wrong"
    )
    @PutMapping("/admin/flats/{flatId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateFlat(@PathVariable flatId: String, @RequestBody flatDto: NewFlatDto) = run {//nie wiem jaka składnia powinna być ale działa
        flatService.updateFlat(flatId, flatDto)
        flatPriceService.updatePriceByFlatId(flatId, flatDto.pricePerNight)
        //facil
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
        description = "Flat with given id was not found.",
    )
    @GetMapping("/flats/{flatId}")
    fun getFlatDetails(@PathVariable flatId: String): ResponseEntity<*> = try {
        ResponseEntity.ok(flatDetailsService.getFlatDetailsById(flatId).toDto())
    } catch (e: FlatNotFoundException) {
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

    @Operation(
        summary = "Get flat offers on web",
        description = "price descending is 1, price ascending is 2 and by newest is 0 wich is also defult"
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
    @GetMapping("/admin/flats")
    fun getAllWebFlats(
        @RequestParam page: Int,
        @RequestParam pageSize: Int,
        @RequestParam name: String?,
        @RequestParam sort: Int?,
    ): ResponseEntity<*> = try {
        val webflatpage = flatService.findAllOrderByName(page, pageSize, name, sort)
        ResponseEntity.ok(webflatpage.toDto(Flat::toDto))
    }
    catch (e: IllegalArgumentException) {
        ResponseEntity.badRequest().body(e.message)
    }
}
