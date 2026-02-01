package com.luzdorefugio.service;

import com.luzdorefugio.domain.Material;
import com.luzdorefugio.domain.Stock;
import com.luzdorefugio.domain.StockMovement;
import com.luzdorefugio.domain.enums.MovementType;
import com.luzdorefugio.dto.admin.AdjustStockRequest;
import com.luzdorefugio.dto.admin.BulkPurchaseRequest;
import com.luzdorefugio.dto.admin.InboundRequest;
import com.luzdorefugio.dto.admin.RestockRequest;
import com.luzdorefugio.exception.BusinessException;
import com.luzdorefugio.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class InventoryService {
    private final MaterialRepository materialRepo;
    private final StockRepository stockRepo;
    private final StockMovementRepository movementRepo;

    public InventoryService(MaterialRepository materialRepo, StockRepository stockRepo,
                            StockMovementRepository movementRepo) {
        this.materialRepo = materialRepo;
        this.stockRepo = stockRepo;
        this.movementRepo = movementRepo;
    }

    public void processInbound(InboundRequest request) {
        // 1. Validar Material
        var material = materialRepo.findById(request.materialId())
                .orElseThrow(() -> new BusinessException("Material not found!"));

        // 2. Buscar Stock Existente ou Criar Novo (Java Stream Style)
        var stock = stockRepo.findByMaterialId(request.materialId())
                .orElseGet(() -> Stock.builder()
                        .material(material)
                        .quantityOnHand(BigDecimal.ZERO)
                        .quantityAllocated(BigDecimal.ZERO)
                        .build());

        // 3. MATEMÁTICA FINANCEIRA: Recalcular Custo Médio Ponderado
        // Se já tínhamos stock, temos de ponderar o preço antigo com o novo
        if (stock.getQuantityOnHand().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal valorTotalAtual = stock.getQuantityOnHand().multiply(material.getAverageCost());
            BigDecimal valorEntrada = request.quantity().multiply(request.unitCost());

            BigDecimal novaQuantidadeTotal = stock.getQuantityOnHand().add(request.quantity());

            // (ValorAntigo + ValorNovo) / QuantidadeTotal
            BigDecimal novoCustoMedio = valorTotalAtual.add(valorEntrada)
                    .divide(novaQuantidadeTotal, 4, RoundingMode.HALF_UP);

            material.setAverageCost(novoCustoMedio);
        } else {
            // Se estava a zero, o custo médio é o preço desta compra
            material.setAverageCost(request.unitCost());
        }

        // 4. Atualizar Quantidades
        stock.setQuantityOnHand(stock.getQuantityOnHand().add(request.quantity()));

        // 5. Auditoria (Log)
        var movement = StockMovement.builder()
                .material(material)
                .type(MovementType.INBOUND)
                .quantity(request.quantity())
                .referenceId(request.purchaseOrder())
                .timestamp(Instant.now())
                .userId("SYSTEM") // Futuro: ID do utilizador logado
                .build();

        // 6. Gravar tudo
        materialRepo.save(material);
        stockRepo.save(stock);
        movementRepo.save(movement);
    }

    @Transactional
    public void reportLoss(AdjustStockRequest request) {
        // 1. Buscar Stock
        Stock stock = stockRepo.findByMaterialId(request.materialId())
                .orElseThrow(() -> new BusinessException("Stock not found"));

        // 2. Validar (Opcional: permitir stock negativo? Para já, vamos bloquear)
        if (stock.getQuantityOnHand().compareTo(request.quantity()) < 0) {
            throw new BusinessException("Não podes registar uma quebra maior que o stock atual.");
        }

        // 3. Atualizar Quantidade
        stock.setQuantityOnHand(stock.getQuantityOnHand().subtract(request.quantity()));
        stockRepo.save(stock);

        // 4. Registar Movimento (Para sabermos para onde foi o material)
        StockMovement movement = StockMovement.builder()
                .material(stock.getMaterial())
                .type(MovementType.ADJUSTMENT)
                .quantity(request.quantity().negate()) // Negativo porque saiu
                .referenceId("LOSS-" + System.currentTimeMillis())
                .userId("SYSTEM")
                .notes(request.reason()) // Guardamos o motivo
                .build();

        movementRepo.save(movement);
    }

    @Transactional
    public void registerBulkPurchase(BulkPurchaseRequest request) {
        String batchId = "COMPRA-" + System.currentTimeMillis(); // ID único para agrupar tudo

        // 1. Processar cada Material (Cera, Pavios, etc.)
        for (BulkPurchaseRequest.ItemPurchase item : request.items()) {
            Stock stock = stockRepo.findByMaterialId(item.materialId())
                    .orElseThrow(() -> new BusinessException("Material não encontrado: " + item.materialId()));

            // Aumentar Stock
            stock.setQuantityOnHand(stock.getQuantityOnHand().add(item.quantity()));
            stockRepo.save(stock);

            // Registar Movimento Financeiro do Item
            StockMovement itemMove = StockMovement.builder()
                    .material(stock.getMaterial())
                    .type(MovementType.INBOUND)
                    .quantity(item.quantity())
                    .totalValue(item.totalCost().negate()) // Valor negativo (Despesa)
                    .referenceId(batchId)
                    .userId("SYSTEM")
                    .notes("Fornecedor: " + request.supplier())
                    .build();
            movementRepo.save(itemMove);
        }

        // 2. Processar Portes de Envio (Se houver)
        if (request.shippingCost() != null && request.shippingCost().compareTo(BigDecimal.ZERO) > 0) {
            // Buscar o Material Especial "Portes"
            Material shippingMat = materialRepo.findById(UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff"))
                    .orElseThrow(() -> new RuntimeException("Material 'Portes' não configurado na BD"));

            StockMovement shippingMove = StockMovement.builder()
                    .material(shippingMat)
                    .type(MovementType.INBOUND)
                    .quantity(BigDecimal.ONE) // Quantidade simbólica 1
                    .totalValue(request.shippingCost().negate()) // O Custo dos Portes
                    .referenceId(batchId)
                    .userId("SYSTEM")
                    .notes("Portes de Envio - " + request.supplier())
                    .build();
            movementRepo.save(shippingMove);
        }
    }

    @Transactional
    public void registerPurchase(RestockRequest request) {
        Material material = materialRepo.findById(request.materialId())
                .orElseThrow(() -> new BusinessException("Material não encontrado na base de dados"));

        // 2. CORREÇÃO: Em vez de 'orElseThrow', usamos 'orElse' para criar se não existir
        Stock stock = stockRepo.findByMaterialId(request.materialId())
                .orElse(Stock.builder()
                        .material(material).quantityOnHand(BigDecimal.ZERO).quantityAllocated(BigDecimal.ZERO)
                        .build());
        // 3. Adiciona a quantidade comprada ao que já existe (0 ou valor anterior)
        BigDecimal novaQuantidade = stock.getQuantityOnHand().add(request.quantity());
        stock.setQuantityOnHand(novaQuantidade);

        if (request.totalCost() != null && request.quantity().compareTo(BigDecimal.ZERO) > 0) {

            // 1. Calcular o Valor Total do que JÁ tinhas no armazém
            // (Qtd Atual * Custo Médio Atual)
            BigDecimal currentTotalValue = stock.getQuantityOnHand()
                    .multiply(material.getAverageCost());

            // 2. Calcular o Valor Total do que ESTÁS A COMPRAR agora
            // (Qtd Nova * Preço Unitário Novo)
            BigDecimal purchaseTotalValue = request.quantity()
                    .multiply(request.totalCost());

            // 3. Somar os Valores (Valor Antigo + Valor Novo)
            BigDecimal finalTotalValue = currentTotalValue.add(purchaseTotalValue);

            // 4. Somar as Quantidades (Qtd Antiga + Qtd Nova)
            BigDecimal finalTotalQty = stock.getQuantityOnHand().add(request.quantity());

            // 5. Calcular a Média (Valor Total / Qtd Total)
            if (finalTotalQty.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal newAverageCost = finalTotalValue.divide(finalTotalQty, 4, RoundingMode.HALF_UP);

                // Atualiza o Material com o novo custo
                material.setAverageCost(newAverageCost);
                // Nota: Não te esqueças de fazer materialRepo.save(material) depois!
            }
        }

        stockRepo.save(stock);

        // 3. Registar o Movimento Financeiro (Despesa)
        StockMovement movement = StockMovement.builder()
                .material(stock.getMaterial())
                .type(MovementType.INBOUND) // Entrada
                .quantity(request.quantity())
                .totalValue(request.totalCost().negate()) // Valor NEGATIVO (Saiu dinheiro)
                .referenceId("COMPRA-" + System.currentTimeMillis())
                .userId("SYSTEM")
                .notes("Fornecedor: " + (request.supplier() != null ? request.supplier() : "N/A"))
                .build();

        movementRepo.save(movement);
    }

    @Transactional
    public void processRestock(UUID materialId, BigDecimal quantity, String notes) {

        // 1. Buscar o Material e o Stock
        var material = materialRepo.findById(materialId)
                .orElseThrow(() -> new BusinessException("Material not found: " + materialId));

        var stock = stockRepo.findByMaterialId(materialId)
                .orElseGet(() -> Stock.builder()
                        .material(material)
                        .quantityOnHand(BigDecimal.ZERO)
                        .quantityAllocated(BigDecimal.ZERO)
                        .build());

        // 2. ATUALIZAR QUANTIDADES (Sem tocar no Custo Médio)
        // A caixa volta para a prateleira. O valor unitário do material mantém-se o que era.
        stock.setQuantityOnHand(stock.getQuantityOnHand().add(quantity));

        // 3. Auditoria (Log)
        // O total_value fica a 0, porque este movimento não custou dinheiro à empresa (já era nosso)
        var movement = StockMovement.builder()
                .material(material)
                .type(MovementType.INBOUND) // Ou criares um tipo ADJUSTMENT/RETURN
                .quantity(quantity)
                .notes(notes) // "Cliente dispensou embalagem"
                .totalValue(BigDecimal.ZERO)
                .timestamp(Instant.now())
                .userId("SYSTEM")
                .build();

        // 4. Gravar
        stockRepo.save(stock);
        movementRepo.save(movement);

        // Opcional: Se usares a lógica de atualizar custos de produtos em cascata,
        // aqui NÃO precisas de chamar, porque o preço do material não mudou.
    }
}