package pw.react.backend.exceptions

sealed class FlatImageException(override val message: String) : Exception(message) {

    data class ThumbnailNotFound(override val message: String) : FlatImageException(message)
    data class ImageNotFound(override val message: String): FlatImageException(message)
}
