package com.flatly.security

import com.flatly.security.models.Credentials

interface AuthenticationService {

    fun authenticate(credentials: Credentials)
}
