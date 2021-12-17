package com.learning.accesscontrol.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.learning.accesscontrol.entity.UserEntity;
import com.learning.accesscontrol.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
public class JwtUtils {
    private final UserRepository userRepository;

    public String getAccessTokenFromHeaderToken(String headerToken) {
        return headerToken.substring("Bearer ".length());
    }

    public UserEntity getUserFromHeaderToken(String headerToken) {
        String accessToken = getAccessTokenFromHeaderToken(headerToken);
        String email = geUserNameFromAccessToken(accessToken);
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        return userEntity.orElse(null);
    }

    private String geUserNameFromAccessToken(String accessToken) {
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(accessToken);
        String username = decodedJWT.getSubject();
        return username;
    }
}
