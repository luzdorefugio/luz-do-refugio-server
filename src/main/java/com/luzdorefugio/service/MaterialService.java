package com.luzdorefugio.service;

import com.luzdorefugio.domain.Material;
import com.luzdorefugio.domain.Stock;
import com.luzdorefugio.domain.enums.TransactionCategory;
import com.luzdorefugio.dto.admin.material.CreateMaterialRequest;
import com.luzdorefugio.dto.admin.material.MaterialPurchaseRequest;
import com.luzdorefugio.dto.admin.material.MaterialResponse;
import com.luzdorefugio.exception.BusinessException;
import com.luzdorefugio.repository.MaterialRepository;
import com.luzdorefugio.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MaterialService {
    private final MaterialRepository materialRepo;
    private final StockRepository stockRepo;
    private final FinancialService financialService;

    public MaterialService(MaterialRepository materialRepo, StockRepository stockRepo,
                           FinancialService financialService) {
        this.materialRepo = materialRepo;
        this.stockRepo = stockRepo;
        this.financialService = financialService;
    }

    // --- 1. LISTAR TODOS ---
    public List<MaterialResponse> getAll() {
        return materialRepo.findAll().stream()
                .map(this::mapToResponse) // Usa o método auxiliar lá de baixo
                .toList();
    }

    // --- 2. BUSCAR POR ID (Novo) ---
    public MaterialResponse getById(UUID id) {
        Material material = materialRepo.findById(id)
                .orElseThrow(() -> new BusinessException("Material not found with ID: " + id));

        return mapToResponse(material);
    }

    // --- 3. CRIAR ---
    @Transactional
    public MaterialResponse createMaterial(CreateMaterialRequest request) {
        if (materialRepo.existsBySku(request.sku())) {
            throw new BusinessException("Material with SKU " + request.sku() + " already exists.");
        }

        Material material = Material.builder()
                .sku(request.sku())
                .name(request.name())
                .description(request.description())
                .type(request.type())
                .unit(request.unit())
                .minStockLevel(request.minStockLevel())
                .active(true)
                .build();

        Material saved = materialRepo.save(material);
        Stock stock = Stock.builder().material(saved).quantityOnHand(request.quantityOnHand())
                        .quantityAllocated(BigDecimal.ZERO).build();
        stock.setQuantityOnHand(request.quantityOnHand());
        stockRepo.save(stock);
        return mapToResponse(saved);
    }

    // --- 4. ATUALIZAR (Novo) ---
    @Transactional
    public MaterialResponse updateMaterial(UUID id, CreateMaterialRequest request) {
        Material material = materialRepo.findById(id)
                .orElseThrow(() -> new BusinessException("Material not found with ID: " + id));

        // Se o SKU mudou, garantir que não colide com outro existente
        if (!material.getSku().equals(request.sku()) && materialRepo.existsBySku(request.sku())) {
            throw new BusinessException("SKU " + request.sku() + " is already in use by another material.");
        }

        // Atualizar campos
        material.setName(request.name());
        material.setSku(request.sku());
        material.setDescription(request.description());
        material.setType(request.type());
        material.setUnit(request.unit());
        material.setMinStockLevel(request.minStockLevel());
        material.setAverageCost(request.averageCost()); // Atualiza custo se vier do form

        Material updatedMaterial = materialRepo.save(material);

        // --- 2. ATUALIZAR O STOCK (A NOVIDADE) ---
        // Procura o registo na tabela 'stocks'. Se não existir, cria um novo (safe guard).
        Stock stock = stockRepo.findByMaterialId(id)
                .orElse(Stock.builder()
                        .material(updatedMaterial)
                        .quantityOnHand(BigDecimal.ZERO)
                        .quantityAllocated(BigDecimal.ZERO)
                        .build());
        stock.setQuantityOnHand(request.quantityOnHand());
        stockRepo.save(stock);
        return mapToResponse(updatedMaterial);
    }

    @Transactional
    public void deleteMaterial(UUID id) {
        Optional<Material> optMaterial = materialRepo.findById(id);
        if (optMaterial.isEmpty()) {
            throw new BusinessException("Material not found with ID: " + id);
        }
        Material material = optMaterial.get();
        material.setActive(false);
        materialRepo.save(material);
    }

    @Transactional
    public void restoreMaterial(UUID id) {
        Optional<Material> optMaterial = materialRepo.findById(id);
        if (optMaterial.isEmpty()) {
            throw new BusinessException("Material not found with ID: " + id);
        }
        Material material = optMaterial.get();
        material.setActive(true);
        materialRepo.save(material);
    }

    @Transactional
    public void processPurchase(UUID materialId, MaterialPurchaseRequest request) {
        // 1. Validar e Buscar Material
        Material material = materialRepo.findById(materialId)
                .orElseThrow(() -> new BusinessException("Material não encontrado"));

        // 2. Atualizar Stock (Entrada)
        Stock stock = stockRepo.findByMaterialId(materialId)
                .orElse(Stock.builder()
                        .material(material)
                        .quantityOnHand(BigDecimal.ZERO)
                        .build());

        stock.setQuantityOnHand(stock.getQuantityOnHand().add(request.quantity()));
        stockRepo.save(stock);

        // 3. Registar Movimento de Stock (Histórico)
        // (Opcional por agora, mas idealmente criarias um StockMovement do tipo INBOUND)

        // 4. Registar a Saída de Dinheiro
        String desc = String.format("Compra de %s %s de %s",
                request.quantity(),
                material.getUnit(),
                material.getName());

        if (request.supplierNote() != null && !request.supplierNote().isEmpty()) {
            desc += " (" + request.supplierNote() + ")";
        }

        financialService.registerExpense(
                request.totalCost(),
                TransactionCategory.MATERIAL_PURCHASE, // Categoria Automática
                desc
        );
    }

    private MaterialResponse mapToResponse(Material material) {
        // Busca o Stock atual na tabela de Stocks
        BigDecimal qty = stockRepo.findByMaterialId(material.getId())
                .map(Stock::getQuantityOnHand)
                .orElse(BigDecimal.ZERO);

        return new MaterialResponse(
                material.getId(),
                material.getSku(),
                material.getName(),
                material.getType(), // Atenção: Confirma se o DTO espera String ou Enum
                material.getUnit(),
                material.getMinStockLevel(),
                material.getAverageCost(),
                qty,
                material.isActive()
        );
    }
}