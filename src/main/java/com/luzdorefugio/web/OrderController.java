package com.luzdorefugio.web;

import com.luzdorefugio.dto.order.OrderFullResponse;
import com.luzdorefugio.dto.order.OrderResponse;
import com.luzdorefugio.dto.order.OrderRequest;
import com.luzdorefugio.dto.order.OrderShopResponse;
import com.luzdorefugio.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @GetMapping("/shop/orders/{id}")
    public ResponseEntity<OrderShopResponse> getOrderByIdShop(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findByIdShop(id));
    }

    @PostMapping("/shop/orders")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(service.createOrderShop(request));
    }

    @GetMapping("/shop/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrdersShop() {
        return ResponseEntity.ok(service.getAllOrders());
    }

    @GetMapping("/admin/orders/{id}")
    public ResponseEntity<OrderFullResponse> getOrderByIdAdmin(@PathVariable UUID id, Principal principal) {
        OrderFullResponse order = service.findById(id);
        Authentication auth = (Authentication) principal;
        if (auth != null) {
            String currentUserEmail = auth.getName();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a ->
                            Objects.equals(a.getAuthority(), "ADMIN") ||
                                    Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
            if (!isAdmin && !order.getCustomerEmail().equals(currentUserEmail)) {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.ok(order);
    }

    @PostMapping("/admin/orders")
    public ResponseEntity<OrderResponse> createAdminOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(service.createOrder(request));
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrdersAdmin() {
        return ResponseEntity.ok(service.getAllOrders());
    }

    @PatchMapping("/admin/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID id, @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");
        return ResponseEntity.ok(service.updateStatus(id, newStatus));
    }

    @GetMapping("/admin/orders/count-pending")
    public ResponseEntity<Long> countPending() {
        return ResponseEntity.ok(service.countByStatus());
    }

    @GetMapping("/admin/orders/pending-list")
    public ResponseEntity<List<OrderResponse>> getPendingOrders() {
        return ResponseEntity.ok(service.getPendingOrdersList());
    }

    @PatchMapping("/admin/orders/{id}/invoice-status")
    public ResponseEntity<OrderResponse> toggleInvoiceStatus(@PathVariable UUID id, @RequestParam boolean issued) {
        return ResponseEntity.ok(service.updateInvoiceStatus(id, issued));
    }
}