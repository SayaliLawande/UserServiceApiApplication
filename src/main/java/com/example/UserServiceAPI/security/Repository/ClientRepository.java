package com.example.UserServiceAPI.security.Repository;

import java.util.Optional;

import com.example.UserServiceAPI.security.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByClientId(String clientId);
}
