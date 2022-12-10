package com.imooc.uaa.security.filter.jwt;

import com.imooc.uaa.config.AppProperties;
import com.imooc.uaa.util.CollectionUtil;
import com.imooc.uaa.util.JwtUtil;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {
  private final AppProperties appProperties;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (checkJwtToken(request)) {
      // TODO:
        validateToken(request).filter( claims -> claims.get("authorities") != null)
            .ifPresentOrElse(claims -> {
                // 有值
                    List<?> authorities = CollectionUtil.convertObjectToList(claims.get("authorities"));
                    //转换为String
                    List<SimpleGrantedAuthority> grantedAuthorityList = authorities.stream().map(String::valueOf)
                        // .map(strThority -> new SimpleGrantedAuthority(strThority))
                        .map(SimpleGrantedAuthority::new).collect(toList());
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(claims.getSubject(), null, grantedAuthorityList);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                },
                () -> {
                // 为空
                });

    }
    filterChain.doFilter(request, response);
  }

  private Optional<Claims> validateToken(HttpServletRequest request) {
    String jwtToken =
        request
            .getHeader(appProperties.getJwt().getHeader())
            .replace(appProperties.getJwt().getPrefix(), "");
    try {
      return Optional.of(
          Jwts.parserBuilder().setSigningKey(JwtUtil.key).build().parseClaimsJws(jwtToken).getBody());
    } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException
         | IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  private boolean checkJwtToken(HttpServletRequest request) {
    String authenticationHeader = request.getHeader(appProperties.getJwt().getHeader());
    if (authenticationHeader != null
        && authenticationHeader.startsWith(appProperties.getJwt().getPrefix())) {
      return true;
    }
    return false;
  }
}
