package com.luzdorefugio.repository;

import com.luzdorefugio.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> {
    Optional<Stock> findByMaterialId(UUID materialId);
}