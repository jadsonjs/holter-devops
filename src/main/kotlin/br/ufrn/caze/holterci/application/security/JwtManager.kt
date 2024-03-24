package br.ufrn.caze.holterci.application.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*


@Component
class JwtManager {

    @Value("\${token.secret}")
    val tokenSecret : String = ""

    val expirationTime = 86400000L // 24 horas

    //val expirationTime = 60000L // 1 minute

    fun generateToken(userName: String, clientIpAddress: String, clientUserAgent: String): String {

        val tokenSecret: String = tokenSecret
        val secretKeyBytes = tokenSecret.toByteArray()
        val secretKey = Keys.hmacShaKeyFor(secretKeyBytes)

        val now = Instant.now()
        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .subject(userName)
            .expiration(Date.from(now.plusMillis(expirationTime)))
            .issuedAt(Date.from(now))
            .signWith(secretKey)
            .claim("ip", clientIpAddress)
            .claim("user-agent", clientUserAgent)
            .compact()
    }

    fun isValid(jwtToken: String, userName: String, clientIpAddress: String, clientUserAgent: String): Boolean {

        try {
            val secretKeyBytes = tokenSecret.toByteArray()
            val key = Keys.hmacShaKeyFor(secretKeyBytes)

            val parser = Jwts.parser()
                .verifyWith(key)
                .build()

            val claims = parser.parseSignedClaims(jwtToken).payload

            val expiration = claims.expiration
            val subject = claims.subject

            val tokenClientIpAddress : String = if ( claims.get("ip") != null ) claims.get("ip") as String else ""
            val tokenClientUserAgent : String = if ( claims.get("user-agent") != null ) claims.get("user-agent") as String else ""

            if (expiration.compareTo(Date()) > 0 && subject.equals(userName) && tokenClientIpAddress.equals(clientIpAddress) && tokenClientUserAgent.equals(clientUserAgent)) {
                return true
            }
        }catch (ex : Exception){
            return false
        }

        return false
    }


}