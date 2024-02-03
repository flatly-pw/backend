package pw.react.backend.services

import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import pw.react.backend.dao.FlatImageRepository
import pw.react.backend.exceptions.FlatImageException
import pw.react.backend.models.domain.FlatImage
import pw.react.backend.models.domain.toDomain

class FlatImageService(private val flatImageRepository: FlatImageRepository) {

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
}
