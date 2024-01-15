package pw.react.backend.models.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table
class UserEntity(
    var name: String,
    var lastName: String,
    var email: String,
    private var password: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null
) : UserDetails {

    override fun getUsername() = email

    override fun getPassword() = password

    fun setPassword(password: String) {
        this.password = password
    }

    override fun getAuthorities() = emptyList<GrantedAuthority>()

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true
}
