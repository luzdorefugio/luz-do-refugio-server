package com.luzdorefugio.service;

import com.luzdorefugio.domain.*;
import com.luzdorefugio.domain.enums.OrderChannel;
import com.luzdorefugio.domain.enums.OrderStatus;
import com.luzdorefugio.dto.order.OrderRequest;
import com.luzdorefugio.dto.order.OrderResponse;
import com.luzdorefugio.exception.BusinessException;
import com.luzdorefugio.exception.ResourceNotFoundException;
import com.luzdorefugio.repository.OrderRepository;
import com.luzdorefugio.repository.ProductRepository;
import com.luzdorefugio.repository.ProductStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductStockRepository productStockRepository;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private PromotionService promotionService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    // Dados comuns para os testes
    private OrderRequest orderRequest;
    private Product product;
    private ProductStock productStock;
    private UUID productId;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        // 1. Setup Produto
        product = Product.builder()
                .id(productId)
                .name("Vela de Lavanda")
                .recipe(Collections.emptyList()) // Lista vazia para evitar NullPointer no fluxo 'withoutBox'
                .build();

        // 2. Setup Stock
        productStock = ProductStock.builder()
                .product(product)
                .quantityOnHand(100) // Stock inicial suficiente
                .build();

        // 3. Setup Request (Dados de entrada)
        OrderRequest.CustomerData customer = new OrderRequest.CustomerData();
        customer.setFullName("João Silva");
        customer.setEmail("joao@email.com");

        OrderRequest.PaymentData payment = new OrderRequest.PaymentData();
        payment.setMethod("CREDIT_CARD");

        OrderRequest.OrderItemDto itemDto = new OrderRequest.OrderItemDto();
        itemDto.setProductId(productId);
        itemDto.setQuantity(2);
        itemDto.setPrice(new BigDecimal("15.00"));

        orderRequest = new OrderRequest();
        orderRequest.setCustomer(customer);
        orderRequest.setPayment(payment);
        orderRequest.setItems(List.of(itemDto));
        orderRequest.setTotal(new BigDecimal("35.00")); // 30 items + 5 shipping
        orderRequest.setShippingCost(new BigDecimal("5.00"));
        orderRequest.setChannel(OrderChannel.WEBSITE);
        orderRequest.setWithoutBox(false); // Default
    }

    // --- CENÁRIO 1: SUCCESS (Caminho Feliz) ---
    @Test
    @DisplayName("Deve criar encomenda com sucesso, decrementar stock e enviar notificação")
    void createOrder_Success() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productStockRepository.findByProductId(productId)).thenReturn(Optional.of(productStock));

        // Simulamos que o save retorna a própria encomenda com um ID gerado
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(orderId);
            return savedOrder;
        });

        // Act
        OrderResponse response = orderService.createOrder(orderRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(orderId);
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);

        // 1. Verificar Decremento de Stock
        ArgumentCaptor<ProductStock> stockCaptor = ArgumentCaptor.forClass(ProductStock.class);
        verify(productStockRepository).save(stockCaptor.capture());

        ProductStock savedStock = stockCaptor.getValue();
        // Tinha 100, pediu 2 -> Deve ter 98
        assertThat(savedStock.getQuantityOnHand()).isEqualTo(98);

        // 2. Verificar Persistência da Encomenda
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getItems()).hasSize(1);
        assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo(new BigDecimal("35.00"));
        assertThat(savedOrder.getShippingCost()).isEqualByComparingTo(new BigDecimal("5.00"));

        // 3. Verificar Notificação (Swallowed exception testada implicitamente se não falhar)
        verify(notificationService).sendOrderConfirmation(
                eq("joao@email.com"),
                eq("João Silva"),
                eq(orderId),
                any(BigDecimal.class)
        );
    }

    @Test
    @DisplayName("Deve validar processamento de cupão se código fornecido")
    void createOrder_WithPromotion_Success() {
        // Arrange
        orderRequest.setAppliedPromotionCode("VERAO2024");
        orderRequest.setDiscountAmount(new BigDecimal("5.00"));

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productStockRepository.findByProductId(productId)).thenReturn(Optional.of(productStock));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(orderId);
            return o;
        });

        // Act
        orderService.createOrder(orderRequest);

        // Assert
        // Verifica se o serviço de promoção foi chamado para incrementar uso
        verify(promotionService).incrementUsage("VERAO2024");
    }

    // --- CENÁRIO 2: ERRO - STOCK INSUFICIENTE ---
    @Test
    @DisplayName("Deve lançar exceção quando quantidade pedida excede o stock")
    void createOrder_InsufficientStock_ThrowsException() {
        // Arrange
        productStock.setQuantityOnHand(1); // Só temos 1
        // O Request pede 2 (definido no setUp)

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productStockRepository.findByProductId(productId)).thenReturn(Optional.of(productStock));

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(orderRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Stock insuficiente");

        // Verify: Garante que nada foi salvo
        verify(productStockRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
        verify(notificationService, never()).sendOrderConfirmation(any(), any(), any(), any());
    }

    // --- CENÁRIO 3: ERRO - PRODUTO NÃO ENCONTRADO ---
    @Test
    @DisplayName("Deve lançar exceção quando o produto não existe na BD")
    void createOrder_ProductNotFound_ThrowsException() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(orderRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Produto não encontrado");

        // Verify
        verify(productStockRepository, never()).findByProductId(any()); // Falha antes de ver o stock
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando não existe registo de stock para o produto")
    void createOrder_StockRecordNotFound_ThrowsException() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productStockRepository.findByProductId(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(orderRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Não existe stock registado");
    }

    // --- CENÁRIO 4: ERRO - CUPÃO INVÁLIDO ---
    @Test
    @DisplayName("Deve falhar a encomenda se o serviço de promoção rejeitar o incremento")
    void createOrder_PromotionServiceError_ThrowsException() {
        // Arrange
        orderRequest.setAppliedPromotionCode("INVALIDO");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productStockRepository.findByProductId(productId)).thenReturn(Optional.of(productStock));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // Simulamos que incrementar o uso lança erro (ex: limite de utilizações atingido)
        doThrow(new RuntimeException("Limite de cupão atingido"))
                .when(promotionService).incrementUsage("INVALIDO");

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(orderRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Limite de cupão atingido");

        // Nota Arquitetural: Embora o repositório 'save' tenha sido chamado antes do erro do cupão no código original,
        // como o método deve ser @Transactional (implícito ou explícito), a exceção aqui garante o rollback.
    }
}