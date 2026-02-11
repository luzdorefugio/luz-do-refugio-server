package com.luzdorefugio.service;


import com.luzdorefugio.domain.*;
import com.luzdorefugio.domain.enums.MaterialType;
import com.luzdorefugio.domain.enums.MovementType;
import com.luzdorefugio.domain.enums.OrderStatus;
import com.luzdorefugio.domain.enums.SpecialCondition;
import com.luzdorefugio.dto.order.*;
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
    private final TelegramService telegramService;

    public OrderFullResponse findById(UUID id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Order not found with ID: " + id));
        return mapToResponseFull(order);
    }

    public OrderShopResponse findByIdShop(UUID id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Order not found with ID: " + id));
        return mapToResponseShop(order);
    }

    public OrderResponse createOrderShop(OrderRequest request) {
        request.setStatus(OrderStatus.PENDING);
        request.setChannel(WEBSITE);
        return createOrder(request);
    }

    // 1. CRIAR ENCOMENDA
    public OrderResponse createOrder(OrderRequest request) {

        // 2. Extrair Moradas (Null Safety é importante)
        var shippingAddr = request.getCustomer().getShippingAddress();
        var billingAddr = request.getCustomer().getBillingAddress();

        // Fallback: Se billing vier null, usa shipping (embora o frontend já trate disto)
        if (billingAddr == null) billingAddr = shippingAddr;

        // 3. Builder
        var orderBuilder = Order.builder()
                // --- Identificação do Cliente ---
                .customerName(request.getCustomer().getFullName())
                .customerEmail(request.getCustomer().getEmail())
                .customerPhone(request.getCustomer().getPhone())
                .customerNif(request.getCustomer().getNif())

                // --- Morada de ENVIO (Física) ---
                .address(shippingAddr.getStreet())
                .city(shippingAddr.getCity())
                .zipCode(shippingAddr.getZip())
                // .country(shippingAddr.getCountry()) // Se tiveres coluna country

                // --- Morada de FATURAÇÃO (Fiscal) - NOVOS CAMPOS ---
                .billingAddress(billingAddr.getStreet())
                .billingCity(billingAddr.getCity())
                .billingZipCode(billingAddr.getZip())

                // --- Pagamento e Logística ---
                .paymentMethod(request.getPayment().getMethod())
                .shippingMethod(request.getShippingMethod())
                .shippingCost(request.getShippingCost())
                .totalAmount(request.getTotal())
                .appliedPromotionCode(request.getAppliedPromotionCode())
                .discountAmount(request.getDiscountAmount())
                .channel(request.getChannel())
                .status(request.getStatus());

        // 4. Lógica de GIFT (Só preenche se o objeto existir)
        if (request.getGiftDetails() != null && Boolean.TRUE.equals(request.getGiftDetails().getIsGift())) {
            orderBuilder
                    .isGift(true)
                    .giftFromName(request.getGiftDetails().getFromName())
                    .giftToName(request.getGiftDetails().getToName())
                    .giftMessage(request.getGiftDetails().getMessage());
        } else {
            orderBuilder.isGift(false);
        }

        Order order = orderBuilder.build();

        // 5. Processamento de Items (Mantém-se praticamente igual)
        List<OrderItem> items = request.getItems().stream().map(itemDto -> {
            // Lógica de "Sem Caixa" (Se ainda usares)
            if (Boolean.TRUE.equals(request.getWithoutBox())) {
                returnPackagingToStock(itemDto.getProductId(), itemDto.getQuantity());
            }

            // Buscar Produto e Validar Stock
            Product product = productRepo.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado ID: " + itemDto.getProductId()));

            ProductStock stock = stockRepo.findByProductId(itemDto.getProductId())
                    .orElseThrow(() -> new BusinessException("Não existe stock registado para este produto."));

            if (stock.getQuantityOnHand() < itemDto.getQuantity()) {
                throw new BusinessException("Stock insuficiente para '" + product.getName() + "'.");
            }

            // Atualizar Stock
            stock.setQuantityOnHand(stock.getQuantityOnHand() - itemDto.getQuantity());
            stockRepo.save(stock);

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .price(itemDto.getPrice())
                    .quantity(itemDto.getQuantity())
                    .build();

        }).collect(Collectors.toList());

        order.setItems(items);
        Order savedOrder = repository.save(order);

        // 6. Pós-Venda (Promoções, Alertas, Emails)
        if (order.getAppliedPromotionCode() != null && !order.getAppliedPromotionCode().isEmpty()) {
            promotionService.incrementUsage(order.getAppliedPromotionCode());
        }

        if (savedOrder.getStatus() == OrderStatus.PAID || savedOrder.getStatus() == OrderStatus.DELIVERED) {
            financialService.registerRevenue(
                    savedOrder.getTotalAmount(),
                    savedOrder.getId().toString(),
                    savedOrder.getCustomerName()
            );
        }

        // Telegram - Se for gift, podes querer avisar no Telegram que é para oferta!
        new Thread(() -> {
            String alertMsg = savedOrder.getIsGift() ? "🎁 NOVA OFERTA VENDIDA!" : "NOVA VENDA!";
            telegramService.enviarAlertaVenda(
                    alertMsg + " ID: " + savedOrder.getId().toString(),
                    savedOrder.getTotalAmount().doubleValue(),
                    savedOrder.getCustomerName()
            );
        }).start();

        // Email de Confirmação
        if (savedOrder.getStatus() == OrderStatus.PENDING) {
            notificationService.sendOrderConfirmation(
                    savedOrder.getCustomerEmail(),
                    savedOrder.getCustomerName(),
                    savedOrder.getId(),
                    savedOrder.getTotalAmount()
            );
        } else {
            // Se for PAID ou DELIVERED, envia o Recibo adaptado
            notificationService.sendOrderPaidReceipt(
                    savedOrder.getCustomerEmail(),
                    savedOrder.getCustomerName(),
                    savedOrder.getId(),
                    savedOrder.getTotalAmount(),
                    savedOrder.getStatus() // <--- Passamos o estado aqui
            );
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

    public Long countByStatus() {
        return repository.countByStatus(OrderStatus.PENDING);
    }

    public List<OrderResponse> getPendingOrdersList() {
        return repository.findByStatusOrderByCreatedAtDesc(OrderStatus.PENDING)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
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
        if (promo.getSpecialCondition() == SpecialCondition.BUY_2_PAY_1) {
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
            if (item.getProduct() == null) {
                throw new BusinessException("Produto não encontrado");
            }
            ProductStock productStock = stockRepo.findByProductId(item.getProduct().getId())
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
                .customerName(order.getCustomerName())
                .createdAt(order.getCreatedAt())
                .customerEmail(order.getCustomerEmail())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .shippingMethod(order.getShippingMethod())
                .shippingCost(order.getShippingCost())
                .appliedPromotionCode(order.getAppliedPromotionCode())
                .discountAmount(order.getDiscountAmount())
                .invoiceIssued(order.isInvoiceIssued())
                .items(order.getItems().stream().map(item ->
                        OrderItemResponse.builder()
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .sku(item.getProduct().getSku())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }

    private OrderShopResponse mapToResponseShop(Order order) {
        return OrderShopResponse.builder()
                .id(order.getId())
                .createdAt(order.getCreatedAt())
                .customerName(order.getCustomerName())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .city(order.getCity())
                .zipCode(order.getZipCode())
                .paymentMethod(order.getPaymentMethod())
                .shippingMethod(order.getShippingMethod())
                .shippingCost(order.getShippingCost())
                .discountAmount(order.getDiscountAmount())
                .appliedPromotionCode(order.getAppliedPromotionCode())
                .invoiceIssued(order.isInvoiceIssued())
                .items(order.getItems().stream().map(item ->
                        OrderItemResponse.builder()
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .sku(item.getProduct().getSku())
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
                .shippingMethod(order.getShippingMethod())
                .shippingCost(order.getShippingCost())
                .appliedPromotionCode(order.getAppliedPromotionCode())
                .discountAmount(order.getDiscountAmount())
                .status(order.getStatus())
                .address(order.getAddress())
                .city(order.getCity())
                .zipCode(order.getZipCode())
                .paymentMethod(order.getPaymentMethod())
                .channel(order.getChannel())
                .invoiceIssued(order.isInvoiceIssued())
                .isGift(order.getIsGift())
                .giftFromName(order.getGiftFromName())
                .giftToName(order.getGiftToName())
                .giftMessage(order.getGiftMessage())
                .items(order.getItems().stream().map(item ->
                        OrderItemResponse.builder()
                                .productId(item.getProduct().getId())
                                .productName(item.getProduct().getName())
                                .sku(item.getProduct().getSku())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }
}