package pw.react.backend.services

import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import pw.react.backend.dao.FlatEntityRepository
import pw.react.backend.dao.FlatImageRepository
import pw.react.backend.exceptions.FlatImageException
import pw.react.backend.exceptions.FlatNotFoundException
import pw.react.backend.exceptions.InvalidFileException
import pw.react.backend.models.domain.FlatImage
import pw.react.backend.models.domain.toDomain
import pw.react.backend.models.domain.toEntity
import java.io.IOException
import kotlin.jvm.optionals.getOrNull

class FlatImageService(
    private val flatImageRepository: FlatImageRepository,
    private val flatEntityRepository: FlatEntityRepository
) {

    fun getThumbnailUriByFlatId(flatId: String): String? {
        val thumbnail = flatImageRepository.findThumbnailByFlatId(flatId) ?: return null
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/flats/$flatId/image/${thumbnail.id}")
            .toUriString()
    }

    fun getImageUrisByFlatId(flatId: String): List<String> {
        val images = flatImageRepository.findFlatImageEntitiesByFlatId(flatId)
        return images.map { image ->
            ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/flats/$flatId/image/${image.id}")
                .toUriString()
        }
    }

    fun getImage(imageId: String, flatId: String): FlatImage {
        val image = flatImageRepository.findFlatImageBy(imageId, flatId)
            ?: throw FlatImageException.ImageNotFound("There was not image for imageId: $imageId and flatId: $flatId")
        return image.toDomain()
    }

    fun saveImage(id: String, imagefile: MultipartFile): FlatImage {

        //null and then smart cast problems so workaround
        val imagepath = imagefile.originalFilename
        if (imagepath == null) throw InvalidFileException("no image path")
        val imagetype = imagefile.contentType
        if (imagetype == null) throw InvalidFileException("no file type")
        // Normalize file name
        val fileName = StringUtils.cleanPath(imagepath)

        try {

            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw InvalidFileException("Sorry! Filename contains invalid path sequence $fileName")
            }

            val newFlatImage = FlatImage("-1", fileName, imagetype, imagefile.bytes )
            val flatEntity = flatEntityRepository.findById(id).getOrNull()
                ?: throw FlatNotFoundException("Flat with id: $id was not found")
            var imglist = flatImageRepository.findFlatImageEntitiesByFlatId(id)
            var ord = -1
            if (imglist.isNotEmpty()) ord = imglist.maxOf { img -> img.ordinal }
            return flatImageRepository.save( newFlatImage.toEntity(flatEntity, ord)).toDomain()
        } catch (ex: IOException) {
            throw InvalidFileException("Could not store file $fileName. Please try again!", ex)
        }
    }
}
