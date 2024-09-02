package com.example.UserServiceAPI.Controller;

import com.example.UserServiceAPI.DTOs.CreateRoleRequestDto;
import com.example.UserServiceAPI.Models.Role;
import com.example.UserServiceAPI.Service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class RoleController {

    RoleService roleService;

    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }

    @PostMapping()
    public ResponseEntity<Role> createRole(CreateRoleRequestDto createRoleRequestDto){
        Role role = roleService.createRole(createRoleRequestDto.getName());
        return new ResponseEntity<>(role,HttpStatus.OK);
    }
}
