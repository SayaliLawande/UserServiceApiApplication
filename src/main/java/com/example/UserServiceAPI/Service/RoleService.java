package com.example.UserServiceAPI.Service;

import com.example.UserServiceAPI.DTOs.CreateRoleRequestDto;
import com.example.UserServiceAPI.DTOs.UserDto;
import com.example.UserServiceAPI.Models.Role;
import com.example.UserServiceAPI.Repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    public Role createRole(String name){
        System.out.println("RoleService : CreateRole()");

        Role role = new Role();
        role.setName(name);
        return roleRepository.save(role);
    }
}

