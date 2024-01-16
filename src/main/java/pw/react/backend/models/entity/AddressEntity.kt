package pw.react.backend.models.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "address_entity")
class AddressEntity(
    var street: String,
    var postalCode: String,
    var city: String,
    var country: String,
    var latitude: Double,
    var longitude: Double,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
) {

    @OneToOne(mappedBy = "address")
    lateinit var flat: FlatEntity
}
