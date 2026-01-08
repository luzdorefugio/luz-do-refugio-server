package com.luzdorefugio.service;

import com.luzdorefugio.domain.*;
import com.luzdorefugio.domain.enums.MovementType;
import com.luzdorefugio.dto.admin.product.CreateProductRequest;
import com.luzdorefugio.dto.admin.product.AdminProductResponse;
import com.luzdorefugio.dto.admin.RecipeItemResponse;
import com.luzdorefugio.dto.shop.ShopProductResponse;
import com.luzdorefugio.exception.BusinessException;
import com.luzdorefugio.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ProductService {
    private final ProductRepository productRepo;
    private final MaterialRepository materialRepo;
    private final StockRepository stockRepo;
    private final StockMovementRepository movementRepo;
    private final ProductStockRepository productStockRepo;
    private final NotificationService notificationService;

    public ProductService(ProductRepository productRepo, MaterialRepository materialRepo,
                          StockRepository stockRepo, StockMovementRepository movementRepo,
                          ProductStockRepository productStockRepo, NotificationService notificationService) {
        this.productRepo = productRepo;
        this.materialRepo = materialRepo;
        this.stockRepo = stockRepo;
        this.movementRepo = movementRepo;
        this.productStockRepo = productStockRepo;
        this.notificationService = notificationService;
    }

    public AdminProductResponse getById(UUID id) {
        return productRepo.findById(id).map(this::mapToResponse)
                .orElseThrow(() -> new BusinessException("Product not found with ID: " + id));
    }

    public List<AdminProductResponse> getAllAdmin() {
        return productRepo.findAll().stream().map(this::mapToResponse).toList();
    }

    public List<ShopProductResponse> getAllShop() {
        return productRepo.findByActiveTrueAndActiveShopTrue().stream().map(this::mapToResponseShop).toList();
    }

    @Transactional
    public Product createProduct(CreateProductRequest request) {
        Product product = Product.builder()
                .sku(request.sku()).name(request.name())
                .price(request.price()).activeShop(request.activeShop()).build();
        if (request.recipeItems() != null) {
            applyRecipeAndCalculateCost(product, request.recipeItems());
        }
        return productRepo.save(product);
    }

    @Transactional
    public AdminProductResponse update(UUID id, CreateProductRequest request) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new BusinessException("Produto não encontrado com ID: " + id));
        if (!product.getSku().equals(request.sku()) && productRepo.existsBySku(request.sku())) {
            throw new BusinessException("Já existe um produto com o SKU: " + request.sku());
        }
        product.setName(request.name());
        product.setSku(request.sku());
        product.setPrice(request.price());
        product.setActiveShop(request.activeShop());
        product.getRecipe().clear();
        if (request.recipeItems() != null) {
            applyRecipeAndCalculateCost(product, request.recipeItems());
        }
        Product saved = productRepo.save(product);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public int calculateMaxProduction(UUID productId) {
        Product product = productRepo.findById(productId).orElseThrow();
        return product.getRecipe().stream()
                .mapToInt(recipeItem -> {
                    BigDecimal required = recipeItem.getQuantityRequired();
                    BigDecimal available = stockRepo.findByMaterialId(recipeItem.getMaterial().getId())
                            .map(Stock::getQuantityOnHand).orElse(BigDecimal.ZERO);
                    if (available.compareTo(BigDecimal.ZERO) <= 0) return 0;
                    return available.divide(required, 0, RoundingMode.DOWN).intValue();
                }).min().orElse(0);
    }

    @Transactional
    public void produceProduct(UUID productId, int quantityToProduce) {
        if (quantityToProduce <= 0) {
            throw new BusinessException("A quantidade a produzir deve ser maior que zero.");
        }
        Product product = productRepo.findByIdWithRecipe(productId)
                .orElseThrow(() -> new BusinessException("Produto com ID " + productId + " não encontrado."));
        if (product.getRecipe().isEmpty()) {
            throw new BusinessException("Este produto não tem receita definida. Não é possível fabricar.");
        }
        String productionBatch = "PROD-" + System.currentTimeMillis(); // ID do Lote para rastreio
        for (ProductRecipe recipeItem : product.getRecipe()) {
            Material material = recipeItem.getMaterial();
            BigDecimal totalRequired = recipeItem.getQuantityRequired()
                    .multiply(new BigDecimal(quantityToProduce));
            Stock stock = stockRepo.findByMaterialId(material.getId())
                    .orElseThrow(() -> new BusinessException("Não existe stock registado para o material: " + material.getName()));
            if (stock.getQuantityOnHand().compareTo(totalRequired) < 0) {
                throw new BusinessException(
                        String.format("Stock insuficiente de %s. Necessário: %s, Disponível: %s",
                                material.getName(), totalRequired, stock.getQuantityOnHand())
                );
            }
            stock.setQuantityOnHand(stock.getQuantityOnHand().subtract(totalRequired));
            stockRepo.save(stock);
            if (stock.getQuantityOnHand().compareTo(stock.getMaterial().getMinStockLevel()) < 0) {
                notificationService.sendLowStockAlert(
                        stock.getMaterial().getName(),
                        stock.getQuantityOnHand(),
                        stock.getMaterial().getMinStockLevel()
                );
            }
            StockMovement movement = StockMovement.builder()
                    .material(material)
                    .type(MovementType.OUTBOUND)
                    .totalValue(BigDecimal.ZERO)
                    .quantity(totalRequired.negate()).referenceId(productionBatch)
                    .userId("SYSTEM").build();
            movementRepo.save(movement);
        }
        ProductStock productStock = productStockRepo.findByProductId(productId)
                .orElseGet(() -> ProductStock.builder().product(product).quantityOnHand(0).build());

        productStock.setQuantityOnHand(productStock.getQuantityOnHand() + quantityToProduce);
        productStockRepo.save(productStock);
    }

    @Transactional
    public void delete(UUID id) {
        Optional<Product> optProduct = productRepo.findById(id);
        if (optProduct.isEmpty()) {
            throw new BusinessException("Product not found with ID: " + id);
        }
        Product product = optProduct.get();
        product.setActive(false);
        productRepo.save(product);
    }

    @Transactional
    public void restore(UUID id) {
        Optional<Product> optProduct = productRepo.findById(id);
        if (optProduct.isEmpty()) {
            throw new BusinessException("Product not found with ID: " + id);
        }
        Product product = optProduct.get();
        product.setActive(true);
        productRepo.save(product);
    }

    private AdminProductResponse mapToResponse(Product product) {
        int maxProduction = this.calculateMaxProduction(product.getId());
        int realStock = productStockRepo.findByProductId(product.getId())
                .map(ProductStock::getQuantityOnHand).orElse(0);
        List<BigDecimal> estimatedCostList = new ArrayList<>();
        List<RecipeItemResponse> recipeDTOs = product.getRecipe().stream()
                .map(item -> {
                    BigDecimal qty = item.getQuantityRequired();
                    BigDecimal unitCost = item.getMaterial().getAverageCost();
                    estimatedCostList.add((qty != null && unitCost != null)
                            ? qty.multiply(unitCost)
                            : BigDecimal.ZERO);
                    return new RecipeItemResponse(
                            item.getMaterial().getId(),
                            item.getMaterial().getName(),
                            qty,
                            unitCost,
                            item.getMaterial().getUnit()
                    );
                })
                .toList();
        BigDecimal estimatedCost = estimatedCostList.stream()
                .filter(Objects::nonNull) // Proteção contra nulos na lista
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (product.getScentProfile() != null){
            return new AdminProductResponse(
                    product.getId(),
                    product.getSku(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    maxProduction,realStock,
                    recipeDTOs,
                    estimatedCost,
                    product.getBurnTime(),
                    product.getIntensity(),
                    product.getScentProfile().getTopNote(),
                    product.getScentProfile().getHeartNote(),
                    product.getScentProfile().getBaseNote(),
                    product.isActiveShop(),
                    product.isActive()
            );
        } else {
            return new AdminProductResponse(
                    product.getId(),
                    product.getSku(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    maxProduction,realStock,
                    recipeDTOs,
                    estimatedCost,
                    product.getBurnTime(),
                    product.getIntensity(),
                    null, null, null,
                    product.isActiveShop(),
                    product.isActive()
            );
        }

    }

    private ShopProductResponse mapToResponseShop(Product product) {
        int stock = productStockRepo.findByProductId(product.getId())
                .map(ProductStock::getQuantityOnHand).orElse(0);
        return new ShopProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                stock,
                product.isActive()
        );
    }

    private void applyRecipeAndCalculateCost(Product product, List<CreateProductRequest.ProductRecipeRequest> itemsReq) {
        BigDecimal totalCost = BigDecimal.ZERO;
        if (itemsReq != null) {
            for (CreateProductRequest.ProductRecipeRequest itemDto : itemsReq) {
                Material material = materialRepo.findById(itemDto.materialId())
                        .orElseThrow(() -> new BusinessException("Material não encontrado"));
                product.addRecipeItem(material, itemDto.quantity());
                BigDecimal lineCost = material.getAverageCost().multiply(itemDto.quantity());
                totalCost = totalCost.add(lineCost);
            }
        }
        product.setEstimatedCost(totalCost);
    }
}