package pw.react.backend.models.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "flat_facility_entity")
class FlatFacilityEntity(
    @Id var name: String,
    @ManyToMany var flats: Set<FlatEntity>
)
