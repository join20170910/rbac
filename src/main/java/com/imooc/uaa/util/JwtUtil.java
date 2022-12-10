package com.imooc.uaa.util;

import com.imooc.uaa.config.AppProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtil {

  // 用于签名的访问令牌的密钥
  public static final Key  key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
  // 用于签名的刷新令牌的密钥
  public static final Key refreshKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

  private final AppProperties appProperties;

  public String createAccessToken(UserDetails userDetails) {
    return createJwtToken(userDetails, appProperties.getJwt().getAccessTokenExpireTime(), key);
  }

  public String createRefreshToken(UserDetails userDetails) {
    return createJwtToken(
        userDetails, appProperties.getJwt().getRefreshTokenExpireTime(), refreshKey);
  }
    public String createAccessTokenWithRefreshToken(String token) {
        return parseClaims(token,refreshKey).map(claims -> Jwts.builder().setClaims(claims)
            .setExpiration(new Date(System.currentTimeMillis() + appProperties.getJwt().getAccessTokenExpireTime()))
            .setIssuedAt(new Date())
            .signWith(key,SignatureAlgorithm.HS512)
            .compact()
        ).orElseThrow(()-> new AccessDeniedException("訪問被拒絕"));
    }
  private Optional<Claims> parseClaims(String token, Key key) {
    try {
      Claims body = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJwt(token).getBody();
      return Optional.of(body);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public boolean validateAccessTokenWithExpiration(String accessToken) {
    return validateToken(accessToken, key, false);
  }

  public boolean validateAccessToken(String accessToken) {
    return validateToken(accessToken, key, true);
  }

  public boolean validateRefreshToken(String refreshToken) {
    return validateToken(refreshToken, refreshKey, true);
  }

  public boolean validateToken(String token, Key key, boolean isExpiredInvalid) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parse(token);
      return true;
    } catch (ExpiredJwtException
        | UnsupportedJwtException
        | MalformedJwtException
        | IllegalArgumentException e) {
      if (e instanceof ExpiredJwtException) {
        return !isExpiredInvalid;
      }
      return false;
    }
  }

  public String createJwtToken(UserDetails userDetails, long timeToExpire, Key key) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .setId("wshen")
        .claim(
            "authorities",
            userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList()))
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + timeToExpire))
        .signWith(key)
        .compact();
  }


}
