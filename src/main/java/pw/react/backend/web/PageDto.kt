package pw.react.backend.web

import kotlinx.serialization.Serializable
import org.springframework.data.domain.Page

@Serializable
data class PageDto<T>(
    val data: T,
    val last: Boolean,
)

fun <T, R> Page<T>.toDto(dataMapper: (T) -> R) = PageDto(content.map(dataMapper), isLast)
