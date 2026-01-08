package com.luzdorefugio.repository;

import com.luzdorefugio.domain.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaterialRepository extends JpaRepository<Material, UUID> {

    Optional<Material> findBySku(String sku);

    // Verificação rápida para validações (evitar duplicados)
    boolean existsBySku(String sku);

    @Query("""
        SELECT COALESCE(SUM(s.quantityOnHand * m.averageCost), 0) 
        FROM Stock s 
        JOIN s.material m
    """)
    BigDecimal getTotalInvested();
}