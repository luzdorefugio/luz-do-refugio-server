package com.luzdorefugio.web.admin;

import com.luzdorefugio.dto.admin.material.CreateMaterialRequest;
import com.luzdorefugio.dto.admin.material.MaterialPurchaseRequest;
import com.luzdorefugio.dto.admin.material.MaterialResponse;
import com.luzdorefugio.service.MaterialService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID; // Importante: Estamos a usar UUID

@RestController
@RequestMapping("/api/materials")
@CrossOrigin(origins = "http://localhost:4200") // Permite o Angular
public class MaterialController {

    private final MaterialService service;

    public MaterialController(MaterialService service) {
        this.service = service;
    }

    // 1. LISTAR TODOS
    @GetMapping
    public ResponseEntity<List<MaterialResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 2. BUSCAR POR ID (Novo - usa UUID)
    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // 3. CRIAR
    @PostMapping
    public ResponseEntity<MaterialResponse> create(@Valid @RequestBody CreateMaterialRequest request) {
        MaterialResponse created = service.createMaterial(request);
        return ResponseEntity
                .created(URI.create("/api/materials/" + created.id()))
                .body(created);
    }

    // 4. ATUALIZAR (Novo)
    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponse> update(@PathVariable UUID id, @Valid @RequestBody CreateMaterialRequest request) {
        MaterialResponse updated = service.updateMaterial(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable UUID id) {
        service.restoreMaterial(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<Void> registerPurchase(
            @PathVariable UUID id,
            @RequestBody @Valid MaterialPurchaseRequest request) {
        service.processPurchase(id, request);
        return ResponseEntity.ok().build();
    }
}