package com.luzdorefugio.repository;

import com.luzdorefugio.domain.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProductStockRepository extends JpaRepository<ProductStock, UUID> {

    // Este método é fundamental: permite encontrar a tabela de stock
    // usando o ID do produto (que é o que vem no pedido de venda)
    Optional<ProductStock> findByProductId(UUID productId);
}