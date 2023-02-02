package com.imooc.uaa.service;

import com.imooc.uaa.domain.Role;
import com.imooc.uaa.repository.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.imooc.uaa.util.Constants.ROLE_ADMIN;
import static com.imooc.uaa.util.Constants.ROLE_STAFF;

@RequiredArgsConstructor
@Service
public class RoleHierarchyService {
    private final RoleRepo roleRepo;
    public String getRoleHierarchyExpr(){
        List<Role> roles = roleRepo.findAll();
        return roles.stream().flatMap(
            role -> role.getPermissions().stream()
                .map(
                    permission -> role.getRoleName() + " > "
                        + permission.getAuthority() + " "
                )
        ).collect(Collectors.joining(" ", ROLE_ADMIN + " > " + ROLE_STAFF + " ", ""));

    }
}
