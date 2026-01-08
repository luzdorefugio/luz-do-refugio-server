package com.luzdorefugio.web;

import com.luzdorefugio.domain.Promotion;
import com.luzdorefugio.service.PromotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PromotionController {
    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    // LISTAR (Admin)
    @GetMapping("/admin/promotions")
    public List<Promotion> getAllPromotions() {
        return promotionService.findAll();
    }

    // DETALHE (Admin - Edição)
    @GetMapping("/admin/promotions/{id}")
    public ResponseEntity<Promotion> getPromotionById(@PathVariable UUID id) {
        return ResponseEntity.ok(promotionService.findById(id));
    }

    // CRIAR (Admin)
    @PostMapping("/admin/promotions")
    public ResponseEntity<Promotion> createPromotion(@RequestBody Promotion promotion) {
        return ResponseEntity.ok(promotionService.create(promotion));
    }

    // EDITAR (Admin)
    @PutMapping("/admin/promotions/{id}")
    public ResponseEntity<Promotion> updatePromotion(@PathVariable UUID id, @RequestBody Promotion promotion) {
        return ResponseEntity.ok(promotionService.update(id, promotion));
    }

    @DeleteMapping("/admin/promotions/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable UUID id) {
        promotionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/admin/promotions/{id}/status")
    public ResponseEntity<Void> toggleStatus(@PathVariable UUID id, @RequestBody Map<String, Boolean> payload) {
        Boolean active = payload.get("active");
        if (active == null) return ResponseEntity.badRequest().build();

        promotionService.toggleStatus(id, active);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/shop/promotions/validate/{code}")
    public ResponseEntity<?> validateCode(@PathVariable String code) {
        try {
            Promotion promo = promotionService.findByCode(code);
            // Retornamos a promo se for válida (o frontend faz a verificação final de valores ou usamos o método isValidForOrder)
            return ResponseEntity.ok(promo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Código inválido ou inexistente");
        }
    }
}