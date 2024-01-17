package pw.react.backend.models.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "price_entity")
class PriceEntity(
    var priceDollars: Double,
    @OneToOne
    @MapsId
    @JoinColumn(name = "flat_entity_id")
    var flat: FlatEntity,
) {
    @Id
    @Column(name = "flat_entity_id")
    var id: String? = null
}
