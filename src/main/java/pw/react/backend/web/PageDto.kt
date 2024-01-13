package pw.react.backend.web

import org.springframework.data.domain.Page

data class PageDto<T>(
    val data: T,
    val isLast: Boolean,
)

fun <T, R> Page<T>.toDto(dataMapper: (T) -> R) = PageDto(content.map(dataMapper), isLast)
