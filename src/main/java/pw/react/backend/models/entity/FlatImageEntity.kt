package pw.react.backend.models.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.io.Serializable

@Entity
@Table(name = "flat_image_entity")
class FlatImageEntity(
    @ManyToOne
    @JoinColumn(name = "flat_entity_id", nullable = false)
    var flat: FlatEntity,
    var fileName: String,
    var fileType: String,
    @Lob @Column(columnDefinition = "MEDIUMBLOB") var bytes: ByteArray,
    @EmbeddedId var id: FlatImageId,
)

@Embeddable
class FlatImageId(
    @Column(name = "image_id") @GeneratedValue(strategy = GenerationType.UUID) var imageId: String? = null,
    var ordinal: Int
): Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FlatImageId

        if (imageId != other.imageId) return false
        if (ordinal != other.ordinal) return false

        return true
    }

    override fun hashCode(): Int {
        var result = imageId.hashCode()
        result = 31 * result + ordinal
        return result
    }
}
