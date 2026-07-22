package com.ieji.rpg.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.ieji.rpg.domain.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;
    public String generateToken(Usuario user){
        Algorithm algorithm;
        try{
            algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("ieji_rpg")
                    .withSubject(user.getEmail())
                    .withExpiresAt(generateExpirationDate())
                    .withClaim("authorities", user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList())
                    .sign(algorithm);
        }catch(JWTCreationException e){
            throw new RuntimeException("Error while authenticating");
        }
    }
    public String validateToken(String token){
        Algorithm algorithm;
        try {
            algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("ieji_rpg")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch(JWTVerificationException e){
            return null;
        }
    }
    private Instant generateExpirationDate(){
        return Instant.now().plus(1, ChronoUnit.HOURS);
    }
}
