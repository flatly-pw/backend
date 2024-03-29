package pw.react.backend.models.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(name = "flat_image_entity", uniqueConstraints = [UniqueConstraint(columnNames = ["ordinal", "flat_entity_id"])])
class FlatImageEntity(
    @ManyToOne
    @JoinColumn(name = "flat_entity_id", nullable = false)
    var flat: FlatEntity,
    var fileName: String,
    var fileType: String,
    @Lob @Column(columnDefinition = "MEDIUMBLOB") var bytes: ByteArray,
    var ordinal: Int,
    @Id @GeneratedValue(generator = "uuid", strategy = GenerationType.UUID) var id: String? = null
)
