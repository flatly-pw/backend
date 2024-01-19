package pw.react.backend.models.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.PrimaryKeyJoinColumn
import jakarta.persistence.Table

@Entity
@Table(name = "flat_entity")
class FlatEntity(
    var title: String,
    var description: String,
    var area: Int,
    var beds: Int,
    var bedrooms: Int,
    var bathrooms: Int,
    var capacity: Int,
    var type: String,
    @OneToOne
    @JoinColumn(name = "address_entity_id", nullable = false)
    var address: AddressEntity,
    @ManyToOne
    @JoinColumn(name = "flat_owner_entity_id")
    var owner: FlatOwnerEntity,
    @Id @GeneratedValue(strategy = GenerationType.UUID) var id: String? = null
) {
    @OneToMany(mappedBy = "flat")
    var images: Set<FlatImageEntity> = emptySet()

    @ManyToMany(mappedBy = "flats")
    var facilities: Set<FlatFacilityEntity> = emptySet()

    @OneToMany(mappedBy = "flat")
    var reviews: Set<FlatReviewEntity> = emptySet()

    @OneToOne(mappedBy = "flat", cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    lateinit var price: PriceEntity
}


