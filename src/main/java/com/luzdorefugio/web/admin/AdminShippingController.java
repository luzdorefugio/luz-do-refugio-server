package com.luzdorefugio.web.admin;

import com.luzdorefugio.domain.ShippingMethod;
import com.luzdorefugio.service.ShippingMethodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/shipping") // Rota protegida
public class AdminShippingController {
    private final ShippingMethodService service;

    public AdminShippingController(ShippingMethodService service) {
        this.service = service;
    }

    @GetMapping
    public List<ShippingMethod> getAllMethods() {
        return service.getAllMethods();
    }

    @PostMapping
    public ResponseEntity<ShippingMethod> create(@RequestBody ShippingMethod method) {
        return ResponseEntity.ok(service.create(method));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShippingMethod> update(@PathVariable UUID id, @RequestBody ShippingMethod method) {
        return ResponseEntity.ok(service.update(id, method));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint extra para toggle rápido
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Void> toggleActive(@PathVariable UUID id) {
        service.toggleActive(id);
        return ResponseEntity.ok().build();
    }
}