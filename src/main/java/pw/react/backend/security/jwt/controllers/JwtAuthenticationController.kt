package pw.react.backend.security.jwt.controllers

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pw.react.backend.security.common.AuthenticationService
import pw.react.backend.security.common.CommonUserDetailsService
import pw.react.backend.security.jwt.models.JwtRequest
import pw.react.backend.security.jwt.models.JwtResponse
import pw.react.backend.security.jwt.services.JwtTokenService

@RestController
@RequestMapping(JwtAuthenticationController.AUTHENTICATION_PATH)
@Profile("jwt")
class JwtAuthenticationController(
    private val authenticationService: AuthenticationService,
    private val jwtTokenService: JwtTokenService,
    private val userDetailsService: CommonUserDetailsService
) {
    @PostMapping("/login")
    fun createAuthenticationToken(
        @RequestBody authenticationRequest: @Valid JwtRequest,
        request: HttpServletRequest
    ): ResponseEntity<*> {
        authenticationService.authenticate(authenticationRequest.mail, authenticationRequest.password)
        val userDetails = userDetailsService.loadUserByUsername(authenticationRequest.mail)
        val token = jwtTokenService.generateToken(userDetails, request)
        return ResponseEntity.ok(JwtResponse(token))
    }

    @PostMapping("/logout")
    fun invalidateToken(request: HttpServletRequest): ResponseEntity<Void> {
        val result = jwtTokenService.invalidateToken(request)
        return if (result) ResponseEntity.accepted().build() else ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
    }

    @DeleteMapping
    fun removeInvalidTokens(): ResponseEntity<Void> {
        jwtTokenService.removeTokens()
        return ResponseEntity.accepted().build()
    }

    companion object {
        const val AUTHENTICATION_PATH = "/auth"
    }
}
