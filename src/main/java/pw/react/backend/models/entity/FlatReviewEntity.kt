package pw.react.backend.models.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class FlatReviewId(
    @Column
    var flatId: String,
    var userId: Long
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FlatReviewId

        if (flatId != other.flatId) return false
        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = flatId.hashCode()
        result = 31 * result + userId.hashCode()
        return result
    }
}
