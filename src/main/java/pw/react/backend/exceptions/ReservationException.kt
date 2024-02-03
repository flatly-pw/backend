package pw.react.backend.exceptions

class ReservationException(override val message: String) : Exception(message)

class ReservationNotFoundException(override val message: String? = null) : Exception(message)

class ReservationCancellationException(override val message: String? = null) : Exception(message)
