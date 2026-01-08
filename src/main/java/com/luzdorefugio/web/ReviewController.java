package com.luzdorefugio.web;

import com.luzdorefugio.dto.admin.review.CreateReviewRequest;
import com.luzdorefugio.dto.admin.review.ReviewResponse;
import com.luzdorefugio.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    // ==========================================
    // ENDPOINTS DA LOJA (PÚBLICOS)
    // ==========================================

    /**
     * Lista apenas as reviews ATIVAS para mostrar no carrossel do site.
     */
    @GetMapping("/shop/reviews")
    public ResponseEntity<List<ReviewResponse>> getShopReviews() {
        return ResponseEntity.ok(service.getAllShop());
    }

    /**
     * Permite a um cliente submeter uma nova review.
     */
    @PostMapping("/shop/reviews")
    public ResponseEntity<ReviewResponse> createReview(@RequestBody CreateReviewRequest request) {
        ReviewResponse created = service.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ==========================================
    // ENDPOINTS DO ADMIN (BACKOFFICE)
    // ==========================================

    /**
     * Lista TODAS as reviews (ativas e inativas) para gestão.
     */
    @GetMapping("/admin/reviews")
    public ResponseEntity<List<ReviewResponse>> getAllReviewsForAdmin() {
        return ResponseEntity.ok(service.getAllAdmin());
    }

    /**
     * "Apaga" uma review (Soft Delete - esconde da loja).
     */
    @DeleteMapping("/admin/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Restaura uma review que estava escondida/apagada.
     */
    @PutMapping("/admin/reviews/{id}/restore")
    public ResponseEntity<Void> restoreReview(@PathVariable UUID id) {
        service.restore(id);
        return ResponseEntity.ok().build();
    }
}