package com.peersignal.app.domain.usecase

import java.security.SecureRandom
import javax.inject.Inject

class GenerateInviteTokenUseCase @Inject constructor() {
    
    fun invoke(): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val secureRandom = SecureRandom()
        
        val randomString = (1..12)
            .map { allowedChars[secureRandom.nextInt(allowedChars.length)] }
            .joinToString("")
            
        // Format as XXXX-XXXX-XXXX for readability
        return "${randomString.substring(0, 4)}-${randomString.substring(4, 8)}-${randomString.substring(8, 12)}"
    }
}
