package com.ananya.transport.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    @Value("${app.jwt.secret}") private String secret;
    @Value("${app.jwt.expiration-ms}") private long expiration;
    public String generateToken(UserDetails user) {
        Date now=new Date();
        return Jwts.builder().subject(user.getUsername()).issuedAt(now).expiration(new Date(now.getTime()+expiration)).signWith(key()).compact();
    }
    public String extractUsername(String token) { return parse(token).getPayload().getSubject(); }
    public boolean isValid(String token, UserDetails user) {
        try { return extractUsername(token).equals(user.getUsername()) && parse(token).getPayload().getExpiration().after(new Date()); }
        catch (JwtException | IllegalArgumentException e) { return false; }
    }
    private Jws<Claims> parse(String token) { return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token); }
    private SecretKey key() { return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)); }
}
