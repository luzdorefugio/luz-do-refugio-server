package com.luzdorefugio.web.admin;

import com.luzdorefugio.domain.Product;
import com.luzdorefugio.dto.admin.product.CreateProductRequest;
import com.luzdorefugio.dto.admin.product.AdminProductResponse;
import com.luzdorefugio.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {
    private final ProductService service;

    public AdminProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AdminProductResponse>> getAll() {
        return ResponseEntity.ok(service.getAllAdmin());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminProductResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody CreateProductRequest request) {
        Product created = service.createProduct(request);
        return ResponseEntity.created(URI.create("/api/admin/products/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminProductResponse> update(@PathVariable UUID id, @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PostMapping("/{id}/produce")
    public ResponseEntity<Void> produce(@PathVariable UUID id, @RequestParam int quantity) {
        service.produceProduct(id, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable UUID id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }
}