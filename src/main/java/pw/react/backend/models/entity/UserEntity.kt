package pw.react.backend.models.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table
class UserEntity(
    var name: String,
    var lastName: String,
    var email: String,
    private var password: String,
    var authority: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null
) : UserDetails {

    @OneToMany(mappedBy = "user")
    var reviews: Set<FlatReviewEntity> = emptySet()

    override fun getUsername() = email

    override fun getPassword() = password

    fun setPassword(password: String) {
        this.password = password
    }

    override fun getAuthorities() = listOf(SimpleGrantedAuthority(authority))

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserEntity

        if (name != other.name) return false
        if (lastName != other.lastName) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (id != other.id) return false
        if (authority != other.authority) return false

        return true
    }
}
