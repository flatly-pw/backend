package com.flatly.security.services

import com.flatly.security.models.Credentials

interface AuthenticationService {

    fun authenticate(credentials: Credentials)
}
