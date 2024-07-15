package com.enset.studentspaymentsmanagement.security;

import com.enset.studentspaymentsmanagement.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.sf.jsqlparser.parser.feature.Feature.use;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;
    private final JwtEncoder jwtEncoder;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public Map<String, String> generateToken(UserDetails userDetails) {

        Consumer<Map<String, Object>> claimsConsumer = claims -> {
            List<String> authorities = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            claims.put("authorities", authorities);
            claims.put("roles", userDetails.getAuthorities());
            claims.put("accountNonExpired", userDetails.isAccountNonExpired());
            claims.put("accountNonLocked", userDetails.isAccountNonLocked());
            claims.put("credentialsNonExpired", userDetails.isCredentialsNonExpired());
            claims.put("enabled", userDetails.isEnabled());
        };
        Instant instant = Instant.now();

        long jwtExpiration = 86400000;
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer(secretKey)
                .claims(claimsConsumer)
                .subject(userDetails.getUsername())
                .issuedAt(instant)
                .expiresAt(instant.plus(jwtExpiration, ChronoUnit.MINUTES))
                .build();
        Map<String, Object> claims = new HashMap<>();
        claimsConsumer.accept(claims);
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS512).build(),
                jwtClaimsSet
        );
        String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
        return Map.of("jwt", jwt);
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        final boolean tokenExpired = isTokenExpired(token);
        System.out.println("Token validation: Username matches = " + username.equals(userDetails.getUsername()) + ", Token expired = " + tokenExpired);
        return (username.equals(userDetails.getUsername())) && !tokenExpired;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Collection<String> addRolePrefix(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(authority -> "ROLE_" + authority.getAuthority())
                .collect(Collectors.toList());
    }

    public Claims decodeJwt(String jwt) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    public Jwt parseJwt(String token) {
        Claims claims = decodeJwt(token);
        return Jwt.withTokenValue(token)
                .header("alg", SignatureAlgorithm.HS512.getValue())
                .claim("authorities", claims.get("authorities"))
                .claim("sub", claims.getSubject())
                .issuedAt(claims.getIssuedAt().toInstant())
                .expiresAt(claims.getExpiration().toInstant())
                .build();
    }
}
