package pw.react.backend.models.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table

@Entity
@Table(name = "flat_image_entity")
class FlatImageEntity(
    var fileName: String,
    var fileType: String,
    @Lob @Column(columnDefinition = "MEDIUMBLOB") var bytes: ByteArray,
    @Id @GeneratedValue(generator = "uuid", strategy = GenerationType.UUID) var id: String,
)
