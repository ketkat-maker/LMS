package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Service.JwtProviderService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import  io.jsonwebtoken.Jwts;

import static io.jsonwebtoken.Jwts.builder;

@Service
@RequiredArgsConstructor
public class JwtProviderServiceImpl implements JwtProviderService
 {
    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final Long jwtExpiresMs = 86400000L;
     public String extractUsername(String token) {
         return extractClaim(token, Claims::getSubject);
     }

     public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
         final Claims claims = extractAllClaims(token);
         return claimsResolver.apply(claims);
     }

     public String generateToken(UserDetails userDetails) {
         return generateToken(new HashMap<>(), userDetails);
     }

     public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
         return buildToken(extraClaims, userDetails, jwtExpiresMs);
     }

     public long getExpirationTime() {
         return jwtExpiresMs;
     }

     private String buildToken(
             Map<String, Object> extraClaims,
             UserDetails userDetails,
             long expiration
     ) {
         return builder()
                 .setClaims(extraClaims)
                 .setSubject(userDetails.getUsername())
                 .setIssuedAt(new Date(System.currentTimeMillis()))
                 .setExpiration(new Date(System.currentTimeMillis() + expiration))
                 .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                 .compact();
     }

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

     private  Claims extractAllClaims(String token) {
         return Jwts
                 .parser()
                 .verifyWith(getSignInKey())
                 .build()
                 .parseClaimsJws(token)
                 .getBody();
     }

     private SecretKey getSignInKey() {
         byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
         return Keys.hmacShaKeyFor(keyBytes);
     }

//    @Override
//    public UserDetails authenticate(String email, String password) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(email, password)
//        );
//
//        return userDetailsService.loadUserByUsername(email);
//    }
//
//    @Override
//    public String generateToken(UserDetails userDetails) {
//        Map<String, Object> claims = new HashMap<>();
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiresMs))
//                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    @Override
//    public UserDetails validToken(String token) {
//            String userName = extractToken(token);
//            return userDetailsService.loadUserByUsername(userName);
//
//    }
//
//    private String extractToken(String token){
//        return Jwts.parser()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//
//    private Key getSigningKey() {
//        byte[] keyBytes = jwtSecret.getBytes();
//        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
//    }

}
