package com.smartmarket.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Map;
import com.smartmarket.backend.config.properties.JwtProperties;

@Component
public class JwtUtil {

    private final JwtProperties props;

    public JwtUtil(JwtProperties props) {
        this.props = props;
    }

    private PrivateKey getPrivateKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(props.getPrivateKeyBase64());
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) {
            throw new IllegalStateException("JWT RSA private key inválida o no configurada", e);
        }
    }

    private PublicKey getPublicKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(props.getPublicKeyBase64());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalStateException("JWT RSA public key inválida o no configurada", e);
        }
    }

    private Key getHmacKey() {
        String secret = props.getSecret() != null && !props.getSecret().isBlank()
                ? props.getSecret()
                : "ZmFrZV9kZXYtc2VjcmV0LXNwcmluZy1ib290LTM=";
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean useRsa() {
        return props.getPrivateKeyBase64() != null && !props.getPrivateKeyBase64().isBlank()
                && props.getPublicKeyBase64() != null && !props.getPublicKeyBase64().isBlank();
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + props.getExpirationMs());
        if (useRsa()) {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(now)
                    .setExpiration(exp)
                    .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                    .compact();
        } else {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(now)
                    .setExpiration(exp)
                    .signWith(getHmacKey(), SignatureAlgorithm.HS256)
                    .compact();
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public Claims getClaims(String token) {
        if (useRsa()) {
            return Jwts.parserBuilder()
                    .setSigningKey(getPublicKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } else {
            return Jwts.parserBuilder()
                    .setSigningKey(getHmacKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
    }
}
