package com.luzdorefugio.service;

import com.luzdorefugio.domain.Promotion;
import com.luzdorefugio.repository.PromotionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    // --- CRUD BÁSICO ---

    public List<Promotion> findAll() {
        return promotionRepository.findAll();
    }

    public Promotion findById(UUID id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promoção não encontrada"));
    }

    public Promotion findByCode(String code) {
        return promotionRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Código inválido: " + code));
    }

    public Promotion create(Promotion promotion) {
        if (promotionRepository.existsByCode(promotion.getCode())) {
            throw new IllegalArgumentException("Já existe uma promoção com o código " + promotion.getCode());
        }
        promotion.setUsedCount(0);
        return promotionRepository.save(promotion);
    }

    public Promotion update(UUID id, Promotion updatedPromo) {
        Promotion existing = findById(id);
        existing.setDiscountType(updatedPromo.getDiscountType());
        existing.setDiscountValue(updatedPromo.getDiscountValue());
        existing.setSpecialCondition(updatedPromo.getSpecialCondition());
        existing.setMinOrderAmount(updatedPromo.getMinOrderAmount());
        existing.setUsageLimit(updatedPromo.getUsageLimit());
        existing.setStartDate(updatedPromo.getStartDate());
        existing.setEndDate(updatedPromo.getEndDate());
        existing.setActive(updatedPromo.isActive());
        return promotionRepository.save(existing);
    }

    public void delete(UUID id) {
        promotionRepository.deleteById(id);
    }

    public void toggleStatus(UUID id, boolean active) {
        Promotion promo = findById(id);
        promo.setActive(active);
        promotionRepository.save(promo);
    }

    // --- LÓGICA DE NEGÓCIO / CÁLCULO ---

    /**
     * Valida se um cupão pode ser aplicado a uma encomenda/carrinho
     */
    public boolean isValidForOrder(String code, BigDecimal orderTotal) {
        Promotion promo = findByCode(code);

        // 1. Está ativo?
        if (!promo.isActive()) return false;

        // 2. Datas
        LocalDateTime now = LocalDateTime.now();
        if (promo.getStartDate() != null && now.isBefore(promo.getStartDate())) return false;
        if (promo.getEndDate() != null && now.isAfter(promo.getEndDate())) return false;

        // 3. Limites de Uso
        if (promo.getUsageLimit() != null && promo.getUsedCount() >= promo.getUsageLimit()) return false;

        // 4. Valor Mínimo
        if (promo.getMinOrderAmount() != null && orderTotal.compareTo(promo.getMinOrderAmount()) < 0) return false;

        return true;
    }

    /**
     * Incrementa o uso (chamar quando a encomenda é PAGA/FINALIZADA)
     */
    public void incrementUsage(String code) {
        Promotion promo = findByCode(code);
        promo.setUsedCount(promo.getUsedCount() + 1);
        promotionRepository.save(promo);
    }
}