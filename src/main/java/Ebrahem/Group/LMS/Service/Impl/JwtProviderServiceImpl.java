package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Service.JwtProviderService;
import Ebrahem.Group.LMS.Utilitys.PublicUtility;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.builder;

@Service
public class JwtProviderServiceImpl implements JwtProviderService {
    private final long jwtAccessExpiresMs;
    private final long jwtRefreshExpiresMs;
    private final String jwtSecret;

    public JwtProviderServiceImpl(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.access.expires}") long jwtAccessExpiresMs,
            @Value("${jwt.refresh.expires}") long jwtRefreshExpiresMs
    ) {
        this.jwtSecret = jwtSecret;
        this.jwtAccessExpiresMs = jwtAccessExpiresMs;
        this.jwtRefreshExpiresMs = jwtRefreshExpiresMs;
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "ACCESS");
        return buildToken(claims, userDetails, jwtAccessExpiresMs, false);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "REFRESH");
        return buildToken(claims, userDetails, jwtRefreshExpiresMs, true);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration,
            boolean includeId
    ) {
        var tokenBuilder = builder()
                .claims().add(extraClaims != null ? extraClaims : Map.of()).and()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256);

        if (includeId) {
            tokenBuilder.id(PublicUtility.generateId());
        }

        return tokenBuilder.compact();
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String extractTokenId(String token) {
        return extractClaim(token, Claims::getId);
    }


    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build().parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
