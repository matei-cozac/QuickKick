package org.registrationservice.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtService {

    // the time that the token will be available;24hours or 86400000 milliseconds
    private static final long EXPIRATION_TIME = 86400000;
    // the secret key used in generating token and refresh token.
    private final SecretKey key;

    public JwtService(@Value("${jtw.string.secret}") String secretString){
        byte[] keyBytes = Decoders.BASE64.decode(secretString);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Method that will generate a jwt token based on an implementation of UserDetails.
     * @param userDetails
     * The implementation on UserDetails which contains the information needed for generating a jwt token.
     * @return
     * The jwt token as a string.
     */
    public String generateToken(UserDetails userDetails){
        return Jwts
                .builder()
                .setClaims(createRoleClaims(userDetails))
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    /**
     * Method that will generate a refresh token based on an implementation of UserDetails. When the jwt
     * token will expire, the client application will use this token to generate a new jwt token.
     * @param userDetails
     * The implementation on UserDetails which contains the information needed for generating a jwt token.
     * @return
     * The refresh token as a string.
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(createRoleClaims(userDetails))
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME * 12))
                .signWith(key)
                .compact();
    }

    /**
     * Method that will create the claims related to roles.
     * @param userDetails
     * The implementation of UserDetails interface.
     * @return
     * A map that will hold the claims only with the roles of the user
     */
    private Map<String,Object> createRoleClaims(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();

        // Convert roles to a list of role names
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles); // Include roles as a claim in the JWT

        return claims;
    }
}
