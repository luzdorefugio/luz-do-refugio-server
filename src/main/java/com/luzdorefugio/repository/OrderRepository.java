package com.luzdorefugio.repository;

import com.luzdorefugio.domain.Order;
import com.luzdorefugio.domain.enums.OrderStatus;
import com.luzdorefugio.dto.admin.SalesByChannelDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByOrderByCreatedAtDesc();

    @Query("""
        SELECT new com.luzdorefugio.dto.admin.SalesByChannelDTO(
            o.channel, 
            SUM(o.totalAmount), 
            COUNT(o)
        ) 
        FROM Order o 
        GROUP BY o.channel
    """)
    List<SalesByChannelDTO> getSalesByChannel();

    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    long countByStatus(OrderStatus status);
}