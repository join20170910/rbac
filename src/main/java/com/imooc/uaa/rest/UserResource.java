package com.imooc.uaa.rest;

import com.imooc.uaa.domain.User;
import com.imooc.uaa.service.UserService;
import com.imooc.uaa.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class UserResource {
    @Autowired
    private UserService userService;
    @GetMapping("/me")
    public String getProfile() {
        return SecurityUtil.getCurrentLogin();
    }

    @GetMapping("/principal")
    public String getCurrentPrincipalName(Principal principal) {
        return principal.getName();
    }

    @GetMapping("/authentication")
    public Authentication getCurrentAuthentication(Authentication authentication) {
        return authentication;
    }
@GetMapping("/users{username}")
    public void getMap(@PathVariable("username") String username){

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/my-email/{email}")
    public User getUserByEmail(@PathVariable("email") String email){
        return userService.findOptionalByEmail(email).orElseThrow();
    }
}
