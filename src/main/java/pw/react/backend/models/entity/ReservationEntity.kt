package pw.react.backend.models.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import java.time.LocalDate

@Entity
@Table(name = "reservation_entity")
class ReservationEntity(
    @ManyToOne
    @JoinColumn(name = "user_entity_id")
    var user: UserEntity,
    @ManyToOne
    @JoinColumn(name = "flat_entity_id")
    var flat: FlatEntity,
    @Temporal(TemporalType.DATE) var startDate: LocalDate,
    @Temporal(TemporalType.DATE) val endDate: LocalDate,
    val adults: Int,
    val children: Int,
    val pets: Int = 0,
    val specialRequests: String? = null,
    var cancelled: Boolean = false,
    var externalUserId: Long? = null,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)
