package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.entities.SignInToken;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.exceptions.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${authentification.jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${authentification.jwt.token-expiration-time}")
    private long jwtExpirationTime;

    @Autowired
    private SignInTokenService signInTokenService;

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public<T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public String generateToken(UserDetails userDetails) {
        Map <String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", userDetails.getAuthorities().toArray()[0].toString());
        return generateToken(extraClaims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return Objects.equals(username, userDetails.getUsername()) && !isTokenExpired(token);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public long getExpirationTime() {
        return jwtExpirationTime;
    }

    public User validateAndGetCurrentUser(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid token");
        }
        String jwtToken = authorizationHeader.substring(7);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        System.out.println("Current user: " + currentUser);
        String existingToken = signInTokenService.getToken(currentUser);
        if (existingToken != null && isTokenValid(existingToken, currentUser) && jwtToken.equals(existingToken)) {
            return currentUser;
        } else {
            throw new InvalidTokenException("Invalid token");
        }
    }
}
