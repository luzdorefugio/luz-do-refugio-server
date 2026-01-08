package com.luzdorefugio.repository;

import com.luzdorefugio.domain.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsBySku(String sku);

    Optional<Product> findBySku(String sku);

    List<Product> findByActiveTrueAndActiveShopTrue();

    @EntityGraph(attributePaths = "recipe")
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithRecipe(@Param("id") UUID id);

    @Query("""
        SELECT COALESCE(SUM(s.quantityOnHand * p.price), 0) 
        FROM ProductStock s 
        JOIN s.product p 
        WHERE p.active = true
    """)
    BigDecimal getTotalStockRevenue();

    @Query("""
        SELECT COALESCE(SUM(s.quantityOnHand * (p.price - COALESCE(p.estimatedCost, 0))), 0) 
        FROM ProductStock s 
        JOIN s.product p 
        WHERE p.active = true
    """)
    BigDecimal getTotalEstimatedProfit();
}