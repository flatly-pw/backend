package pw.react.backend.web

import org.springframework.data.domain.Page

data class PageDto<T>(
    val data: T,
    val isLast: Boolean,
)

fun <T> Page<T>.toDto() = PageDto(content, isLast)
