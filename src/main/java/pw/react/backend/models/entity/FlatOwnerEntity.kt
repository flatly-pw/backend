package pw.react.backend.models.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "flat_owner_entity")
class FlatOwnerEntity(
    var name: String,
    var lastName: String,
    var email: String,
    var phoneNumber: String,
    var registeredAt: LocalDate,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null
)
