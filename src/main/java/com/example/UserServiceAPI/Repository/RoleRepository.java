package com.example.UserServiceAPI.Repository;

import com.example.UserServiceAPI.Models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role,Long> {
    List<Role> findAllByIdIn(List<Long> roleIds);
}
