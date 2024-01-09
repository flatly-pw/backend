package pw.react.backend.security.jwt.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.security.common.AuthenticationService;
import pw.react.backend.security.common.CommonUserDetailsService;
import pw.react.backend.security.jwt.models.JwtRequest;
import pw.react.backend.security.jwt.models.JwtResponse;
import pw.react.backend.security.jwt.services.JwtTokenService;

@RestController
@RequestMapping(path = JwtAuthenticationController.AUTHENTICATION_PATH)
@Profile({"jwt"})
public class JwtAuthenticationController {

    public static final String AUTHENTICATION_PATH = "/auth";

    private final AuthenticationService authenticationService;
    private final JwtTokenService jwtTokenService;
    private final CommonUserDetailsService userDetailsService;

    public JwtAuthenticationController(AuthenticationService authenticationService, JwtTokenService jwtTokenService, CommonUserDetailsService userDetailsService) {
        this.authenticationService = authenticationService;
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest,
                                                       HttpServletRequest request) throws Exception {

        authenticationService.authenticate(authenticationRequest.username(), authenticationRequest.password());
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username());
        String token = jwtTokenService.generateToken(userDetails, request);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<Void> invalidateToken(HttpServletRequest request) {
        boolean result = jwtTokenService.invalidateToken(request);
        return result ? ResponseEntity.accepted().build() : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeInvalidTokens() {
        jwtTokenService.removeTokens();
        return ResponseEntity.accepted().build();
    }
}
