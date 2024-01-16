package pw.react.backend.models.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "price_entity")
class PriceEntity(
    var priceDollars: Double,
    @Id @GeneratedValue var id: Long? = null
) {

    @OneToOne
    @JoinColumn(name = "flat_entity_id")
    lateinit var flat: FlatEntity
}
