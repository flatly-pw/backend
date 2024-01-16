package pw.react.backend.models.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.io.Serializable

@Entity
@Table(name = "flat_review_entity")
class FlatReviewEntity(
    var review: String,
    var rating: Int,
    @EmbeddedId var id: FlatReviewId
) {

    @OneToMany
    @JoinColumn(name = "flat_entity_id")
    lateinit var flatEntity: Set<FlatEntity>

    @OneToMany
    @JoinColumn(name = "user_entity_id")
    lateinit var userEntity: Set<UserEntity>
}

@Embeddable
class FlatReviewId(
    @Column
    var flatId: String,
    var userId: Long
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
