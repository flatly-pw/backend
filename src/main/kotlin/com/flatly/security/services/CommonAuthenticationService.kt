package com.flatly.security.services

import com.flatly.security.models.Credentials
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

class CommonAuthenticationService(private val authenticationManager: AuthenticationManager) : AuthenticationService {

    override fun authenticate(credentials: Credentials) {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(credentials.username, credentials.password)
            )
        } catch (e: DisabledException) {
            throw Exception("USER_DISABLED", e)
        } catch (e: BadCredentialsException) {
            throw Exception("INVALID_CREDENTIALS", e)
        }
    }
}
