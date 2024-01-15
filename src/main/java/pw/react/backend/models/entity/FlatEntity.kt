package pw.react.backend.models.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "flat_entity")
class FlatEntity(
    var description: String,
    var area: Int,
    var bedrooms: Int,
    var bathrooms: Int,
    var capacity: Int,
    @Id @GeneratedValue(strategy = GenerationType.UUID) var id: String? = null
)


