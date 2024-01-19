package pw.react.backend.services

import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import pw.react.backend.dao.FlatImageRepository
import pw.react.backend.exceptions.FlatImageException

class FlatImageService(private val flatImageRepository: FlatImageRepository) {

    fun getThumbnailUriByFlatId(flatId: String): String {
        val thumbnail = flatImageRepository.findThumbnailByFlatId(flatId)
            ?: throw FlatImageException.ThumbnailNotFound("There was not thumbnail for flatId: $flatId")
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/flats/$flatId/image/${thumbnail.id.imageId}")
            .toUriString()
    }

    fun getImageUrisByFlatId(flatId: String): List<String> {
        val images = flatImageRepository.findFlatImageEntitiesByFlatId(flatId)
        return images.map { image ->
            ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/flats/$flatId/image/${image.id.imageId}")
                .toUriString()
        }
    }
}
