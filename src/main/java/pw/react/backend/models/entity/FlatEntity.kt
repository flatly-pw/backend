package pw.react.backend.models.entity

import jakarta.persistence.*

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
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "address_entity_id", nullable = false)
    var address: AddressEntity,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "flat_owner_entity_id")
    var owner: FlatOwnerEntity,
    @Id @GeneratedValue(strategy = GenerationType.UUID) var id: String? = null
) {
    @OneToMany(mappedBy = "flat")
    var images: Set<FlatImageEntity> = emptySet()

    @ManyToMany
    var facilities: Set<FlatFacilityEntity> = emptySet()

    @OneToMany(mappedBy = "flat")
    var reviews: Set<FlatReviewEntity> = emptySet()

    @OneToOne(mappedBy = "flat", cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    private lateinit var price: PriceEntity
}


