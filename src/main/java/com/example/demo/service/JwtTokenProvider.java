package com.example.demo.service;

import com.example.demo.model.entity.User;
import com.example.demo.model.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenProvider {
    private UserRepository userRepository;

    @Value("${jwt.secret-key}")
    private String secretKeyString;
    private Key secretKey;

    final static private long TOKEN_VALID_TIME = 30 * 60 * 1000L;

    @Autowired
    JwtTokenProvider(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 특정 유저에 대한 JWT Token 을 생성합니다.
     *
     * @param userId user 의 user ID.
     * @return JWT Token 값.
     */
    public String createToken(String userId) {
        return createToken(userId, TOKEN_VALID_TIME);
    }

    /**
     * 특정 유저에 대한 JWT Token 을 생성합니다.
     *
     * @param userId user 의 user ID.
     * @param tokenValidTime 토큰 만료까지 유효한 시간.
     * @return JWT Token 값.
     */
    public String createToken(String userId, long tokenValidTime) {
        Claims claims = Jwts.claims().setId(userId);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setIssuer("demo")
                .setAudience("demo")
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(secretKey)
                .compact();
    }

    /**
     * JWT Token 값을 파싱해서 유저를 가져옵니다.
     *
     * @param token JWT Token.
     * @return Token 의 유저. Invalid 한 JWT 일 경우 Empty 값을 리턴합니다.
     */
    public Optional<User> parseToken(String token) {
        // Authorization Header 에서 앞에 Bearer 가 붙는 경우가 있으므로
        // 이를 무시하고, JWT Token 만 파싱합니다.
        String[] splitToken = token.split(" ");
        String pureToken = splitToken[splitToken.length - 1];

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(pureToken)
                    .getBody();

            // token payload 의 jti 값에 user id 가 담기며, 이것으로
            // DB 에서 유저를 가져옵니다.
            String userId = claims.getId();
            return userRepository.findById(userId);
        } catch (JwtException e) {
            // 그 외, 잘못된 토큰인 경우, 토큰 만료기간이 끝난 경우 등
            // 비정상적인 토큰인 경우 empty 값을 반환합니다.
            return Optional.empty();
        }
    }
}
