package com.luzdorefugio.service;

import com.luzdorefugio.domain.Review;
import com.luzdorefugio.dto.admin.review.CreateReviewRequest;
import com.luzdorefugio.dto.admin.review.ReviewResponse; // Assumi este DTO de resposta
import com.luzdorefugio.exception.BusinessException;
import com.luzdorefugio.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository repo;

    public ReviewService(ReviewRepository repo) {
        this.repo = repo;
    }

    /**
     * Lista todas as reviews (Ativas e Inativas) para o Backoffice (Admin)
     */
    public List<ReviewResponse> getAllAdmin() {
        return repo.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Lista apenas as reviews ATIVAS para a Loja (Shop)
     */
    public List<ReviewResponse> getAllShop() {
        return repo.findByActiveTrue().stream() // O teu repositório deve ter este método findByActiveTrue()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Cria uma nova review (usado pelo cliente ou admin)
     */
    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        Review review = Review.builder()
                .authorName(request.authorName())
                .content(request.content())
                .rating(request.rating())
                .active(request.active()).build();
        Review saved = repo.save(review);
        return mapToResponse(saved);
    }

    /**
     * "Apaga" uma review (Soft Delete - apenas desativa para não aparecer na loja)
     */
    @Transactional
    public void delete(UUID id) {
        Review review = repo.findById(id)
                .orElseThrow(() -> new BusinessException("Review não encontrada com ID: " + id));

        review.setActive(false);
        repo.save(review);
    }

    /**
     * Restaura uma review desativada
     */
    @Transactional
    public void restore(UUID id) {
        Review review = repo.findById(id)
                .orElseThrow(() -> new BusinessException("Review não encontrada com ID: " + id));

        review.setActive(true);
        repo.save(review);
    }

    /**
     * Mapper auxiliar para converter Entidade -> DTO
     */
    private ReviewResponse mapToResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getAuthorName(),
                review.getContent(),
                review.getRating(),
                review.isActive()
        );
    }
}