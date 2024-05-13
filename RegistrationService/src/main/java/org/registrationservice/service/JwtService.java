package org.registrationservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
     * Extract the username from the jwt token.
     * @param token
     * The jwt token from which we extract the username
     * @return
     * The username associated with the jwt token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Method that will check if the jwt token expired. If true, the user needs to use the refresh token
     * to generate a new valid jwt token.
     * @param token
     * The token which we need to check if it is expired.
     * @return
     * The expiration status.
     */
    public boolean isTokenExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }

    /**
     * Method that will extract all the claims associated with the jwt token.
     * @param token
     * The token from which we want to extract the claims.
     * @return
     * An Claims object.
     */
    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Method that will extract a specific claim from the token.
     * @param token
     * The jwt token from which we want to extract the specific claim
     * @param claimResolver
     * A resolver which will help us to extract a specific claim.
     * @return
     * The specific claim.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /**
     * Method that will extract the expiration claim from the token.
     * @param token
     * Token from which we extract the expiration token.
     * @return
     * The expiration claim, which is of type Date.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
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
    /**
     * Method that will extract the Authorities(Roles) from the jwt token.
     * @param token
     * The jwt token attached to the http request.
     * @return
     * A list of authorities that the user has.
     */
    public List<SimpleGrantedAuthority> extractAuthoritiesFromToken(String token) {
        Claims claims = extractAllClaims(token);
        List<?> roles = claims.get("roles", List.class); // Ensure this matches the claim name you used when creating the token

        // Process the raw list, safely casting to String where possible

        return roles.stream()
                .filter(role -> role instanceof String)
                .map(role -> new SimpleGrantedAuthority(((String) role)))
                .collect(Collectors.toList());
    }
}
