package com.smartshop.repository;

import com.smartshop.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    boolean existsByEmail(String email);
    Optional<Client> findByEmail(String email);
}

