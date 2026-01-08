package com.luzdorefugio.service;

import com.luzdorefugio.domain.Material;
import com.luzdorefugio.domain.Stock;
import com.luzdorefugio.domain.enums.MaterialType;
import com.luzdorefugio.dto.admin.material.CreateMaterialRequest;
import com.luzdorefugio.dto.admin.material.MaterialResponse;
import com.luzdorefugio.exception.BusinessException;
import com.luzdorefugio.repository.MaterialRepository;
import com.luzdorefugio.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepo;

    @Mock
    private StockRepository stockRepo;

    @InjectMocks
    private MaterialService materialService;

    // Dados de teste
    private Material material;
    private UUID materialId;
    private CreateMaterialRequest createRequest;

    @BeforeEach
    void setUp() {
        materialId = UUID.randomUUID();

        material = Material.builder()
                .id(materialId)
                .sku("CERA-SOJA")
                .name("Cera de Soja")
                .type(MaterialType.CERA) // Ajusta conforme o teu Enum
                .unit("g")               // Ajusta conforme o teu Enum
                .minStockLevel(new BigDecimal("10"))
                .averageCost(new BigDecimal("5.00"))
                .build();

        // Request genérico para create/update
        BigDecimal quantity = new BigDecimal("50");
        BigDecimal cost = new BigDecimal("5.00");
        createRequest = new CreateMaterialRequest(
                "CERA-SOJA",
                "Cera de Soja",
                "Descrição teste",
                MaterialType.CERA,
                "g" ,
                new BigDecimal("10"), quantity, cost
        );
    }

    @Test
    @DisplayName("Deve listar materiais e mapear a quantidade de stock corretamente")
    void getAll_ReturnsListWithStock() {
        // Arrange
        when(materialRepo.findAll()).thenReturn(List.of(material));

        // Simular que este material tem 100 unidades em stock
        Stock stock = Stock.builder().material(material).quantityOnHand(new BigDecimal("100")).build();
        when(stockRepo.findByMaterialId(materialId)).thenReturn(Optional.of(stock));

        // Act
        List<MaterialResponse> response = materialService.getAll();

        // Assert
        assertThat(response).hasSize(1);
        assertThat(response.getFirst().sku()).isEqualTo("CERA-SOJA");
        assertThat(response.getFirst().quantityOnHand()).isEqualByComparingTo(new BigDecimal("100"));
    }

    @Test
    @DisplayName("Deve retornar material por ID com sucesso")
    void getById_Success() {
        // Arrange
        when(materialRepo.findById(materialId)).thenReturn(Optional.of(material));
        when(stockRepo.findByMaterialId(materialId)).thenReturn(Optional.empty()); // Sem stock registado

        // Act
        MaterialResponse response = materialService.getById(materialId);

        // Assert
        assertThat(response.id()).isEqualTo(materialId);
        assertThat(response.quantityOnHand()).isEqualByComparingTo(BigDecimal.ZERO); // Default logic
    }

    @Test
    @DisplayName("Deve lançar exceção se ID não existe")
    void getById_NotFound_ThrowsException() {
        // Arrange
        when(materialRepo.findById(materialId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> materialService.getById(materialId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Material not found");
    }

    // --- 3. CRIAR ---

    @Test
    @DisplayName("Deve criar material com sucesso se SKU for único")
    void createMaterial_Success() {
        // Arrange
        when(materialRepo.existsBySku("CERA-SOJA")).thenReturn(false);
        // O método save retorna o objeto salvo (neste caso, o mock 'material')
        when(materialRepo.save(any(Material.class))).thenReturn(material);

        // Mock do mapToResponse final (stock a zero para material novo)
        when(stockRepo.findByMaterialId(materialId)).thenReturn(Optional.empty());

        // Act
        MaterialResponse response = materialService.createMaterial(createRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.sku()).isEqualTo("CERA-SOJA");
        verify(materialRepo).save(any(Material.class));
    }

    @Test
    @DisplayName("Deve impedir criação se SKU já existe")
    void createMaterial_DuplicateSku_ThrowsException() {
        // Arrange
        when(materialRepo.existsBySku("CERA-SOJA")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> materialService.createMaterial(createRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");

        verify(materialRepo, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar material e atualizar registo de stock existente")
    void updateMaterial_Success_UpdatesStock() {
        // Arrange
        // 1. Encontrar Material
        when(materialRepo.findById(materialId)).thenReturn(Optional.of(material));

        // 2. Mock do Save
        when(materialRepo.save(any(Material.class))).thenReturn(material);

        // 3. Encontrar Stock Existente
        Stock existingStock = Stock.builder()
                .id(UUID.randomUUID()).material(material).quantityOnHand(BigDecimal.ZERO).build();
        when(stockRepo.findByMaterialId(materialId)).thenReturn(Optional.of(existingStock));

        // Act
        materialService.updateMaterial(materialId, createRequest);

        // Assert
        // A. Verificar se o Material foi salvo
        ArgumentCaptor<Material> materialCaptor = ArgumentCaptor.forClass(Material.class);
        verify(materialRepo).save(materialCaptor.capture());
        assertThat(materialCaptor.getValue().getName()).isEqualTo("Cera de Soja");

        // B. Verificar se o Stock foi salvo com a nova quantidade (50 vindo do createRequest)
        ArgumentCaptor<Stock> stockCaptor = ArgumentCaptor.forClass(Stock.class);
        verify(stockRepo).save(stockCaptor.capture()); // Atenção: stockRepo.save é chamado 2x? Não, no updateMaterial chama 1x explicitamente.

        assertThat(stockCaptor.getValue().getQuantityOnHand()).isEqualByComparingTo(new BigDecimal("50"));
    }

    @Test
    @DisplayName("Deve atualizar material e CRIAR registo de stock se não existir")
    void updateMaterial_NoStockExists_CreatesStock() {
        // Arrange
        when(materialRepo.findById(materialId)).thenReturn(Optional.of(material));
        when(materialRepo.save(any(Material.class))).thenReturn(material);

        // Stock não encontrado
        when(stockRepo.findByMaterialId(materialId)).thenReturn(Optional.empty());

        // Act
        materialService.updateMaterial(materialId, createRequest);

        // Assert
        ArgumentCaptor<Stock> stockCaptor = ArgumentCaptor.forClass(Stock.class);
        verify(stockRepo).save(stockCaptor.capture());

        // Verifica se criou um novo stock com o valor do request
        assertThat(stockCaptor.getValue().getQuantityOnHand()).isEqualByComparingTo(new BigDecimal("50"));
        assertThat(stockCaptor.getValue().getMaterial()).isEqualTo(material);
    }

    @Test
    @DisplayName("Deve falhar atualização se alterar SKU para um já existente em outro produto")
    void updateMaterial_SkuConflict_ThrowsException() {
        // Arrange
        Material original = Material.builder().id(materialId).sku("SKU-ORIGINAL").build();
        CreateMaterialRequest updateReq = new CreateMaterialRequest("SKU-DO-VIZINHO", null, null, null, null, null, null, null);

        when(materialRepo.findById(materialId)).thenReturn(Optional.of(original));
        when(materialRepo.existsBySku("SKU-DO-VIZINHO")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> materialService.updateMaterial(materialId, updateReq))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already in use");

        verify(materialRepo, never()).save(any());
        verify(stockRepo, never()).save(any());
    }

    /* --- 5. APAGAR ---

    @Test
    @DisplayName("Deve apagar material se existir")
    void deleteMaterial_Success() {
        // Arrange
        when(materialRepo.existsById(materialId)).thenReturn(true);

        // Act
        materialService.deleteMaterial(materialId);

        // Assert
        verify(materialRepo).deleteById(materialId);
    }*/

  /*  @Test
    @DisplayName("Deve falhar ao apagar se ID não existe")
    void deleteMaterial_NotFound_ThrowsException() {
        // Arrange
        when(materialRepo.existsById(materialId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> materialService.deleteMaterial(materialId))
                .isInstanceOf(BusinessException.class);

        verify(materialRepo, never()).deleteById(any());
    }*/
}