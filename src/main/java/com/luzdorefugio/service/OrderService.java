package com.luzdorefugio.service;


import com.luzdorefugio.domain.*;
import com.luzdorefugio.domain.enums.MaterialType;
import com.luzdorefugio.domain.enums.MovementType;
import com.luzdorefugio.domain.enums.OrderStatus;
import com.luzdorefugio.domain.enums.SpecialCondition;
import com.luzdorefugio.dto.order.OrderFullResponse;
import com.luzdorefugio.dto.order.OrderItemResponse;
import com.luzdorefugio.dto.order.OrderResponse;
import com.luzdorefugio.dto.order.OrderRequest;
import com.luzdorefugio.exception.BusinessException;
import com.luzdorefugio.exception.ResourceNotFoundException;
import com.luzdorefugio.repository.OrderRepository;
import com.luzdorefugio.repository.ProductRepository;
import com.luzdorefugio.repository.ProductStockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.luzdorefugio.domain.enums.OrderChannel.WEBSITE;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository repository;
    private final ProductRepository productRepo;
    private final ProductStockRepository stockRepo;
    private final InventoryService inventoryService;
    private final PromotionService promotionService;
    private final NotificationService notificationService;
    private final FinancialService financialService;

    public OrderFullResponse findById(UUID id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Order not found with ID: " + id));
        return mapToResponseFull(order);
    }

    public OrderResponse createOrderShop(OrderRequest request) {
        request.setStatus(OrderStatus.PENDING);
        request.setChannel(WEBSITE);
        return createOrder(request);
    }

    // 1. CRIAR ENCOMENDA
    public OrderResponse createOrder(OrderRequest request) {
        var order = Order.builder()
                .customerName(request.getCustomer().getName())
                .customerEmail(request.getCustomer().getEmail())
                .address(request.getCustomer().getAddress())
                .city(request.getCustomer().getCity())
                .zipCode(request.getCustomer().getZipCode())
                .customerPhone(request.getCustomer().getPhone())
                .customerNif(request.getCustomer().getNif())
                .paymentMethod(request.getPayment().getPaymentMethod())
                .shippingMethod(request.getShippingMethod()).shippingCost(request.getShippingCost())
                .totalAmount(request.getTotal())
                .appliedPromotionCode(request.getAppliedPromotionCode()).discountAmount(request.getDiscountAmount())
                .channel(request.getChannel()).status(OrderStatus.PENDING).build();
        List<OrderItem> items = request.getItems().stream().map(itemDto -> {
            if (Boolean.TRUE.equals(request.getWithoutBox())) {
                returnPackagingToStock(itemDto.getProductId(), itemDto.getQuantity());
            }
            // A. Buscar o Produto na BD
            Product product = productRepo.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado ID: " + itemDto.getProductId()));
            ProductStock stock = stockRepo.findByProductId(itemDto.getProductId())
                    .orElseThrow(() -> new BusinessException("Não existe stock registado para este produto. Fabrique primeiro!"));
            if (stock.getQuantityOnHand() < itemDto.getQuantity()) {
                throw new BusinessException("Stock insuficiente para '" + product.getName() +
                        "'. Tem: " + stock.getQuantityOnHand() + ", Pedido: " + itemDto.getQuantity());
            }
            stock.setQuantityOnHand(stock.getQuantityOnHand() - itemDto.getQuantity());
            stockRepo.save(stock);

            // D. Criar o OrderItem
            return OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .productName(product.getName())
                    .price(itemDto.getPrice())
                    .quantity(itemDto.getQuantity())
                    .build();

        }).collect(Collectors.toList());
        order.setItems(items);
        Order savedOrder = repository.save(order);
        if (order.getAppliedPromotionCode() != null && !order.getAppliedPromotionCode().isEmpty()) {
            promotionService.incrementUsage(order.getAppliedPromotionCode());
        }
        if (OrderStatus.DELIVERED == request.getStatus()) {
            this.updateStatus(order.getId(), request.getStatus().name());
        }
        try {
            notificationService.sendOrderConfirmation(
                savedOrder.getCustomerEmail(),
                savedOrder.getCustomerName(),
                savedOrder.getId(),
                savedOrder.getTotalAmount());
        } catch (Exception e) {
            logger.error("Erro ao enviar email de estado: ",e);
        }
        return mapToResponse(savedOrder);
    }

    // 2. LISTAR TODAS
    public List<OrderResponse> getAllOrders() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateStatus(UUID orderId, String statusName) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Encomenda não encontrada"));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = OrderStatus.valueOf(statusName);

        // --- VALIDAÇÃO DE SEGURANÇA (Opcional, mas recomendado) ---
        // Impede cancelar encomendas que já foram pagas (deve-se usar RETURNED)
        if (newStatus == OrderStatus.CANCELLED && oldStatus != OrderStatus.PENDING) {
            throw new IllegalStateException("Encomendas já pagas/enviadas devem passar para 'RETURNED' e não 'CANCELLED'.");
        }

        // =================================================================
        // 1. GESTÃO DE STOCK (Inventário)
        // =================================================================

        // CASO A: Estamos a anular ou devolver -> O stock volta para a prateleira
        boolean isReturningToStock = (newStatus == OrderStatus.CANCELLED || newStatus == OrderStatus.RETURNED);
        boolean wasAlreadyReturned = (oldStatus == OrderStatus.CANCELLED || oldStatus == OrderStatus.RETURNED);

        if (isReturningToStock && !wasAlreadyReturned) {
            registerStockMovement(order, MovementType.RETURN, "Anulação/Devolução Enc. #" + orderId);
        }
        // CASO B: Reativação (Correção de erro) -> O stock sai novamente
        else if (wasAlreadyReturned && !isReturningToStock) {
            registerStockMovement(order, MovementType.OUTBOUND, "Reativação Enc. #" + orderId);
        }
        boolean isNewRevenue = (newStatus == OrderStatus.PAID) ||
                (oldStatus == OrderStatus.PENDING && newStatus == OrderStatus.DELIVERED);

        // Garantimos que não duplicamos receita se já estava num estado pago
        // (A menos que venha de RETURNED/CANCELLED, aí é uma nova entrada)
        boolean wasPaidState = (oldStatus == OrderStatus.PAID || oldStatus == OrderStatus.SHIPPED || oldStatus == OrderStatus.DELIVERED);

        if (isNewRevenue && !wasPaidState) {
            financialService.registerRevenue(
                    order.getTotalAmount(),
                    order.getId().toString(),
                    order.getCustomerName()
            );
        }

        // --- SAÍDA DE DINHEIRO (REEMBOLSO) ---
        // Acontece APENAS no estado RETURNED
        if (newStatus == OrderStatus.RETURNED && oldStatus != OrderStatus.RETURNED) {
            financialService.registerRefund(
                    order.getTotalAmount(),
                    order.getId().toString(),
                    order.getCustomerName()
            );
        }

        // =================================================================
        // 3. FINALIZAÇÃO
        // =================================================================
        order.setStatus(newStatus);
        Order updated = repository.save(order);

        try {
            notificationService.sendOrderStatusUpdate(
                    updated.getCustomerEmail(),
                    updated.getCustomerName(),
                    updated.getId(),
                    newStatus
            );
        } catch (Exception e) {
            logger.error("Erro ao enviar email de estado: ", e);
        }

        return mapToResponse(updated);
    }

    @Transactional
    public OrderResponse updateInvoiceStatus(UUID id, boolean issued) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encomenda não encontrada"));
        order.setInvoiceIssued(issued);
        Order updated = repository.save(order);
        return mapToResponse(updated);
    }

    public BigDecimal calculateDiscount(Order order, Promotion promo) {

        // 1. Validações Básicas
        if (!promo.isValid()) throw new RuntimeException("Cupão inválido ou expirado");

        if (promo.getMinOrderAmount() != null &&
                order.getTotalAmount().compareTo(promo.getMinOrderAmount()) < 0) {
            throw new RuntimeException("Valor mínimo não atingido para este cupão");
        }

        BigDecimal discount = BigDecimal.ZERO;
        //BigDecimal subTotal = order.getItemsTotal(); // Soma dos produtos

        // 2. Lógica por Tipo
        switch (promo.getDiscountType()) {
            case FREE_SHIPPING:
                // O desconto é igual ao valor dos portes
                //discount = order.getShippingCost();
                break;

            case FIXED_AMOUNT:
                discount = promo.getDiscountValue();
                break;
            case PERCENTAGE:
                // total * (valor / 100)
               // discount = subTotal.multiply(promo.getDiscountValue())
                //        .divide(new BigDecimal(100));
                break;
        }
        // 3. Lógica Especial (Ex: 2 por 1)
        if (promo.getSpecialCondition() == SpecialCondition.BUY_2_PAY_1) {
            // Lógica complexa: Encontrar o item mais barato e oferecer esse valor
            // (Requer iterar sobre os items da encomenda)
            BigDecimal cheapestItem = order.getItems().stream()
                    .map(OrderItem::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            if (order.getItems().size() >= 2) {
                discount = cheapestItem;
            }
        }
        // Garante que o desconto não é superior ao total (não pagamos ao cliente para levar coisas!)
        if (discount.compareTo(order.getTotalAmount()) > 0) {
            discount = order.getTotalAmount();
        }
        return discount;
    }

    private void registerStockMovement(Order order, MovementType type, String reason) {
        for (OrderItem item : order.getItems()) {
            ProductStock productStock = stockRepo.findByProductId(item.getProductId())
                    .orElseThrow(() -> new BusinessException("Produto não encontrado"));
            if (MovementType.RETURN.equals(type)) {
                productStock.setQuantityOnHand(productStock.getQuantityOnHand() + item.getQuantity());
            } else {
                productStock.setQuantityOnHand(productStock.getQuantityOnHand() - item.getQuantity());
            }
            stockRepo.save(productStock);
            /* TODO: 1. Criar o registo histórico (A TUA FONTE DA VERDADE)
            StockMovement movement = new StockMovement();
            movement.setProduct(product); // ou setMaterial, dependendo da tua lógica de stock
            movement.setType(type);
            movement.setQuantity(item.getQuantity()); // A quantidade do item
            movement.setReason(reason);
            movement.setTimestamp(Instant.now());

            // Gravar o movimento
            stockMovementRepository.save(movement);*/
        }
    }

    private void returnPackagingToStock(UUID productId, int quantitySold) {
        // 1. Buscar o produto e a sua receita
        Product product = productRepo.findById(productId).orElseThrow();

        // 2. Procurar na receita se existe algum material daquela categoria (BOX ou CARD)
        product.getRecipe().stream()
                .filter(recipeItem -> recipeItem.getMaterial().getType() == MaterialType.EMBALAGEM)
                .findFirst()
                .ifPresent(recipeItem -> {
                    // 3. Calcular quanto vamos devolver
                    // Se a receita diz que leva 1 caixa, e vendemos 2 velas, devolvemos 2 caixas.
                    BigDecimal quantityToReturn = recipeItem.getQuantityRequired()
                            .multiply(BigDecimal.valueOf(quantitySold));

                    // 4. Chamar o StockService para adicionar stock (INBOUND)
                    inventoryService.processRestock(recipeItem.getMaterial().getId(),
                            quantityToReturn,
                            "Recuperado na Venda (Cliente dispensou embalagem)"
                    );
                });
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .createdAt(order.getCreatedAt())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .invoiceIssued(order.isInvoiceIssued())
                .items(order.getItems().stream().map(item ->
                        OrderItemResponse.builder()
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }

    private OrderFullResponse mapToResponseFull(Order order) {
        return OrderFullResponse.builder()
                .id(order.getId())
                .createdAt(order.getCreatedAt())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .customerNif(order.getCustomerNif())
                .customerPhone(order.getCustomerPhone())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .address(order.getAddress())
                .city(order.getCity())
                .zipCode(order.getZipCode())
                .paymentMethod(order.getPaymentMethod())
                .channel(order.getChannel())
                .invoiceIssued(order.isInvoiceIssued())
                .items(order.getItems().stream().map(item ->
                        OrderItemResponse.builder()
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }
}