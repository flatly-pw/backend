package pw.react.backend.utils

import kotlinx.datetime.Instant

fun interface TimeProvider {

    operator fun invoke(): Instant
}
