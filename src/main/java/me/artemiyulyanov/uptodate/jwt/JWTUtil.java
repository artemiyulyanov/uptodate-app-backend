package me.artemiyulyanov.uptodate.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JWTUtil {
    public static final long ACCESS_TOKEN_EXPIRATION = 1000 * 3600 * 24; // 15 minutes is an expiration timeline for access token
    public static final long REFRESH_TOKEN_EXPIRATION = 1000 * 3600 * 24; // 24 hours is an expiration timeline for refresh token
    public static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        List<String> roleNames = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles", roleNames);
        claims.put("username", userDetails.getUsername());
        claims.put("scope", "ACCESS");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        List<String> roleNames = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles", roleNames);
        claims.put("username", userDetails.getUsername());
        claims.put("scope", "REFRESH");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return getAllClaimsFromToken(token).get("username", String.class);
    }

    public List<String> extractAuthorities(String token) {
        return getAllClaimsFromToken(token).get("roles", List.class);
    }

    public String extractScope(String token) {
        return getAllClaimsFromToken(token).get("scope", String.class);
    }

    public Date extractExpirationDate(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            extractUsername(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }
}