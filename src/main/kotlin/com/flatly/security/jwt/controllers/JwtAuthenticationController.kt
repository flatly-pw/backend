package com.flatly.security.jwt.controllers

import com.flatly.security.jwt.services.JwtTokenService
import com.flatly.security.models.Credentials
import com.flatly.security.models.JwtResponse
import com.flatly.security.services.AuthenticationService
import com.flatly.security.services.CommonUserDetailsService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(name = JwtAuthenticationController.AUTHENTICATION_PATH)
@Profile("jwt")
class JwtAuthenticationController(
    private val authenticationService: AuthenticationService,
    private val jwtTokenService: JwtTokenService,
    private val userDetailsService: CommonUserDetailsService,
) {

    @PostMapping("/login")
    fun createAuthenticationToken(
        @Valid @RequestBody credentials: Credentials,
        request: HttpServletRequest
    ): JwtResponse {
        authenticationService.authenticate(credentials)
        val userDetails = userDetailsService.loadUserByUsername(credentials.username)
        val token = jwtTokenService.generateToken(userDetails, request)
        return JwtResponse(token)
    }

    companion object {

        const val AUTHENTICATION_PATH = "/auth"
    }
}
