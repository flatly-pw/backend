package pw.react.backend.models.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import java.io.Serializable
import java.time.LocalDate

@Entity
@Table(name = "flat_review_entity")
class FlatReviewEntity(
    @EmbeddedId var id: FlatReviewId,
    @ManyToOne
    @MapsId("flatId")
    @JoinColumn(name = "flat_entity_id")
    var flat: FlatEntity,
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_entity_id")
    var user: UserEntity,
    var review: String,
    var rating: Int,
    var date: LocalDate
)

@Embeddable
class FlatReviewId(
    @Column(name = "flat_id") var flatId: String,
    @Column(name = "user_id") var userId: Long
) : Serializable {

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
