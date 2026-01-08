package com.luzdorefugio.service;

import com.luzdorefugio.domain.*;
import com.luzdorefugio.dto.admin.product.AdminProductResponse;
import com.luzdorefugio.dto.admin.product.CreateProductRequest;
import com.luzdorefugio.exception.BusinessException;
import com.luzdorefugio.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepo;
    @Mock private MaterialRepository materialRepo;
    @Mock private StockRepository stockRepo; // Stock de matérias-primas
    @Mock private StockMovementRepository movementRepo;
    @Mock private ProductStockRepository productStockRepo; // Stock de produtos acabados
    @Mock private NotificationService notificationService;

    @InjectMocks
    private ProductService productService;

    // Dados de teste
    private Product product;
    private Material waxMaterial;
    private Material wickMaterial;
    private UUID productId;
    private UUID waxId;
    private UUID wickId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        waxId = UUID.randomUUID();
        wickId = UUID.randomUUID();

        // 1. Materiais
        waxMaterial = Material.builder()
                .id(waxId)
                .name("Cera de Soja")
                .averageCost(new BigDecimal("10.00")) // Custo por unidade
                .minStockLevel(new BigDecimal("10"))
                .build();

        wickMaterial = Material.builder()
                .id(wickId)
                .name("Pavio Algodão")
                .averageCost(new BigDecimal("0.50"))
                .minStockLevel(new BigDecimal("50"))
                .build();

        // 2. Produto Base
        product = Product.builder()
                .id(productId)
                .sku("VELA-LAVANDA")
                .name("Vela Lavanda")
                .price(new BigDecimal("20.00"))
                .active(true)
                .recipe(new ArrayList<>()) // Lista mutável
                .build();
    }

    // --- 1. CRIAÇÃO E ATUALIZAÇÃO ---

    @Test
    @DisplayName("Deve criar produto e calcular custo estimado baseado na receita")
    void createProduct_WithRecipe_CalculatesCost() {
        // Arrange
        var recipeItemReq = new CreateProductRequest.ProductRecipeRequest(waxId, new BigDecimal("0.2")); // 200g de cera
        var request = new CreateProductRequest("VELA-LAVANDA", "Vela",
                new BigDecimal("20"), List.of(recipeItemReq), true);

        when(materialRepo.findById(waxId)).thenReturn(Optional.of(waxMaterial));
        when(productRepo.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Product created = productService.createProduct(request);

        // Assert
        assertThat(created).isNotNull();
        // Custo esperado: 0.2 (qtd) * 10.00 (custo material) = 2.00
        assertThat(created.getEstimatedCost()).isEqualByComparingTo(new BigDecimal("2.00"));
        verify(productRepo).save(any(Product.class));
    }

    @Test
    @DisplayName("Update deve falhar se mudar SKU para um já existente")
    void update_DuplicateSku_ThrowsException() {
        // Arrange
        var request = new CreateProductRequest("SKU-DUPLICADO", "Nome",  BigDecimal.TEN, null, true);

        when(productRepo.findById(productId)).thenReturn(Optional.of(product));
        when(productRepo.existsBySku("SKU-DUPLICADO")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> productService.update(productId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe um produto com o SKU");

        verify(productRepo, never()).save(any());
    }

    // --- 2. CÁLCULO DE PRODUÇÃO MÁXIMA ---

    @Test
    @DisplayName("Deve calcular produção máxima baseada no material limitante (Gargalo)")
    void calculateMaxProduction_ReturnsCorrectLimit() {
        // Arrange
        // Receita: 0.2 Cera + 1 Pavio
        product.addRecipeItem(waxMaterial, new BigDecimal("0.2"));
        product.addRecipeItem(wickMaterial, new BigDecimal("1.0"));

        when(productRepo.findById(productId)).thenReturn(Optional.of(product));

        // Cenário: 
        // Temos 2.0 de Cera (Dá para 10 velas: 2.0 / 0.2)
        // Temos 5 Pavios (Dá para 5 velas: 5 / 1) -> ESTE É O LIMITE

        Stock waxStock = Stock.builder().material(waxMaterial).quantityOnHand(new BigDecimal("2.0")).build();
        Stock wickStock = Stock.builder().material(wickMaterial).quantityOnHand(new BigDecimal("5.0")).build();

        when(stockRepo.findByMaterialId(waxId)).thenReturn(Optional.of(waxStock));
        when(stockRepo.findByMaterialId(wickId)).thenReturn(Optional.of(wickStock));

        // Act
        int maxProd = productService.calculateMaxProduction(productId);

        // Assert
        assertThat(maxProd).isEqualTo(5);
    }

    @Test
    @DisplayName("Deve retornar 0 se não houver receita")
    void calculateMaxProduction_NoRecipe_ReturnsZero() {
        // Arrange
        product.setRecipe(new ArrayList<>());
        when(productRepo.findById(productId)).thenReturn(Optional.of(product));

        // Act
        int maxProd = productService.calculateMaxProduction(productId);

        // Assert
        assertThat(maxProd).isEqualTo(0);
    }

    // --- 3. PRODUÇÃO (PRODUCE PRODUCT) ---

    @Test
    @DisplayName("Produção Sucesso: Decrementa matérias-primas e incrementa produto acabado")
    void produceProduct_Success() {
        // Arrange
        int qtdToProduce = 10;

        // Receita: 1 Cera por vela
        product.addRecipeItem(waxMaterial, new BigDecimal("1.0"));

        // Stock Atual de Matéria Prima: Tem 100, vai gastar 10 (1.0 * 10)
        Stock currentWaxStock = Stock.builder()
                .material(waxMaterial)
                .quantityOnHand(new BigDecimal("100"))
                .build();

        // Stock Atual de Produto Acabado: 0
        ProductStock currentProductStock = ProductStock.builder()
                .product(product)
                .quantityOnHand(0)
                .build();

        when(productRepo.findByIdWithRecipe(productId)).thenReturn(Optional.of(product));
        when(stockRepo.findByMaterialId(waxId)).thenReturn(Optional.of(currentWaxStock));
        when(productStockRepo.findByProductId(productId)).thenReturn(Optional.of(currentProductStock));

        // Act
        productService.produceProduct(productId, qtdToProduce);

        // Assert
        // 1. Verificações de Matéria Prima (StockRepository)
        ArgumentCaptor<Stock> stockCaptor = ArgumentCaptor.forClass(Stock.class);
        verify(stockRepo).save(stockCaptor.capture());

        Stock savedRawStock = stockCaptor.getValue();
        assertThat(savedRawStock.getQuantityOnHand()).isEqualByComparingTo(new BigDecimal("90")); // 100 - 10

        // 2. Verificações de Movimento (MovementRepository)
        verify(movementRepo).save(any(StockMovement.class));

        // 3. Verificações de Produto Acabado (ProductStockRepository)
        ArgumentCaptor<ProductStock> productStockCaptor = ArgumentCaptor.forClass(ProductStock.class);
        verify(productStockRepo).save(productStockCaptor.capture());

        ProductStock savedProductStock = productStockCaptor.getValue();
        assertThat(savedProductStock.getQuantityOnHand()).isEqualTo(10); // 0 + 10
    }

    @Test
    @DisplayName("Produção Erro: Deve lançar exceção se não houver matéria-prima suficiente")
    void produceProduct_InsufficientMaterial_ThrowsException() {
        // Arrange
        int qtdToProduce = 10;
        product.addRecipeItem(waxMaterial, new BigDecimal("1.0")); // Precisa de 10 total

        // Só temos 5 em stock
        Stock currentWaxStock = Stock.builder()
                .material(waxMaterial)
                .quantityOnHand(new BigDecimal("5"))
                .build();

        when(productRepo.findByIdWithRecipe(productId)).thenReturn(Optional.of(product));
        when(stockRepo.findByMaterialId(waxId)).thenReturn(Optional.of(currentWaxStock));

        // Act & Assert
        assertThatThrownBy(() -> productService.produceProduct(productId, qtdToProduce))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Stock insuficiente");

        // Verify: Garante que nada foi salvo (Rollback lógico)
        verify(stockRepo, never()).save(any());
        verify(productStockRepo, never()).save(any());
        verify(movementRepo, never()).save(any());
    }

    @Test
    @DisplayName("Produção Erro: Produto sem receita não pode ser fabricado")
    void produceProduct_NoRecipe_ThrowsException() {
        // Arrange
        product.setRecipe(Collections.emptyList());
        when(productRepo.findByIdWithRecipe(productId)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThatThrownBy(() -> productService.produceProduct(productId, 5))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("não tem receita definida");
    }

    @Test
    @DisplayName("Produção Alerta: Deve notificar se stock de matéria-prima ficar abaixo do mínimo")
    void produceProduct_TriggersLowStockNotification() {
        // Arrange
        // Mínimo é 10 (definido no setUp). Temos 12. Gastamos 5. Restam 7 (< 10).
        product.addRecipeItem(waxMaterial, new BigDecimal("1.0"));

        Stock currentStock = Stock.builder()
                .material(waxMaterial)
                .quantityOnHand(new BigDecimal("12"))
                .build();

        when(productRepo.findByIdWithRecipe(productId)).thenReturn(Optional.of(product));
        when(stockRepo.findByMaterialId(waxId)).thenReturn(Optional.of(currentStock));
        when(productStockRepo.findByProductId(productId)).thenReturn(Optional.of(ProductStock.builder().product(product).quantityOnHand(0).build()));

        // Act
        productService.produceProduct(productId, 5);

        // Assert
        verify(stockRepo).save(any()); // O save ocorre
        verify(notificationService).sendLowStockAlert(
                eq("Cera de Soja"),
                argThat(bd -> bd.compareTo(new BigDecimal("7")) == 0), // Novo stock
                eq(new BigDecimal("10")) // Nível minimo
        );
    }

    // --- 4. MAPPER (GET) ---

    @Test
    @DisplayName("GetById deve mapear corretamente incluindo MaxProduction")
    void getById_MapsResponseCorrectly() {
        // Arrange
        product.addRecipeItem(waxMaterial, new BigDecimal("1.0"));

        when(productRepo.findById(productId)).thenReturn(Optional.of(product));

        // Mock para o calculateMaxProduction interno e stocks
        Stock stock = Stock.builder().material(waxMaterial).quantityOnHand(new BigDecimal("100")).build();
        when(stockRepo.findByMaterialId(waxId)).thenReturn(Optional.of(stock));
        when(productStockRepo.findByProductId(productId)).thenReturn(Optional.of(ProductStock.builder().quantityOnHand(50).build()));

        // Act
        AdminProductResponse response = productService.getById(productId);

        // Assert
        assertThat(response.sku()).isEqualTo("VELA-LAVANDA");
        assertThat(response.stock()).isEqualTo(50); // Do ProductStockRepo
        assertThat(response.maxProduction()).isEqualTo(100); // 100 stock / 1 req
        assertThat(response.recipeItems()).hasSize(1);
    }
}