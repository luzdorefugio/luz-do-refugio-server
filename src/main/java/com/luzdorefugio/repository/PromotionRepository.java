package com.luzdorefugio.repository;

import com.luzdorefugio.domain.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

    // Para encontrar o cupão quando o cliente o escreve no checkout
    Optional<Promotion> findByCode(String code);

    // Para impedir criar dois códigos iguais no Admin
    boolean existsByCode(String code);
}