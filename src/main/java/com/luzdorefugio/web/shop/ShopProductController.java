package com.luzdorefugio.web.shop;

import com.luzdorefugio.domain.Product;
import com.luzdorefugio.dto.admin.product.AdminProductResponse;
import com.luzdorefugio.dto.admin.product.CreateProductRequest;
import com.luzdorefugio.dto.shop.ShopProductResponse;
import com.luzdorefugio.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shop/products")
public class ShopProductController {
    private final ProductService service;

    public ShopProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ShopProductResponse>> getAllActive() {
        return ResponseEntity.ok(service.getAllShop());
    }


    @GetMapping("/{id}")
    public ResponseEntity<AdminProductResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody CreateProductRequest request) {
        Product created = service.createProduct(request);
        return ResponseEntity.created(URI.create("/api/products/" + created.getId())).body(created);
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
}