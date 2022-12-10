package com.imooc.uaa.utils;

import com.imooc.uaa.UaaApplicationTests;
import com.imooc.uaa.domain.Role;
import com.imooc.uaa.domain.User;
import com.imooc.uaa.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class JwtUtilUnitTest extends UaaApplicationTests {
    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    public void setup(){
    System.out.println("test 000000 -- test ");
    }
    @Test
    @DisplayName("token 构建 测试")
    public void demo01(){
        String username = "john";
        Set<Role> authorityList = Set.of(Role.builder().authority("ROLE_ADMIN")
                .build(),
            Role.builder().authority("ROLE_USER").build());
        User user = User.builder().build();
        user.setUsername(username);
        user.setAuthorities(authorityList);
        String jwtToken = jwtUtil.createJwtToken(user,60_000,JwtUtil.key);
        Assertions.assertFalse(StringUtils.isEmpty(jwtToken));

        Claims body = Jwts.parserBuilder().setSigningKey(JwtUtil.key).build().parseClaimsJws(jwtToken).getBody();
        assertEquals(username,body.getSubject(),"解析后用户名信息");
    }
}
