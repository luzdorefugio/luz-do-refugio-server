package com.luzdorefugio.repository;

import com.luzdorefugio.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Método mágico do Spring Data JPA
    Optional<User> findByEmail(String email);

    long countByActiveTrue();

}