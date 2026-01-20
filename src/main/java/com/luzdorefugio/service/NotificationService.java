package com.luzdorefugio.service;

import com.luzdorefugio.domain.enums.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    // MUDANÇA: Em vez de JavaMailSender, usamos o nosso serviço HTTP
    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    public void sendLowStockAlert(String materialName, BigDecimal currentQty, BigDecimal minQty) {
        logger.info("A enviar alerta de stock para: {}", materialName);
        String text = String.format("""
            Olá Gestor,
            
            O stock do material '%s' desceu abaixo do nível mínimo!
            
            Quantidade Atual: %s
            Mínimo Definido: %s
            
            Por favor, encomenda mais material.
            
            Cumprimentos,
            Sistema Luz do Refúgio 🕯️
            """, materialName, currentQty, minQty);

        emailService.sendEmail("admin@luzdorefugio.pt", "⚠️ ALERTA: Stock Crítico - " + materialName, text);
    }

    @Async
    public void sendOrderConfirmation(String toEmail, String customerName, UUID orderId, BigDecimal total) {
        if (toEmail == null) return;
        logger.info("A enviar confirmação de encomenda #{} para {}", orderId, toEmail);

        String linkRastreio = "https://luzdorefugio.pt/loja/rastreio/" + orderId.toString();
        String text = String.format("""
            Olá %s,
            
            Obrigado pela tua encomenda! ✨
            Recebemos o teu pedido #%s no valor total de %.2f€.
            
            Se escolheste MBWAY ou Transferência, verifica os dados de pagamento na tua área de cliente ou na página de sucesso da compra.
            A tua encomenda será processada assim que confirmarmos o pagamento.
            
            Podes acompanhar o estado aqui: <a href="%s">Ver Encomenda</a>
            
            Com carinho,
            A Equipa Luz do Refúgio
            """, customerName, orderId, total, linkRastreio);

        emailService.sendEmail(toEmail, "Luz do Refúgio - Encomenda Recebida #" + orderId.toString().substring(0, 8), text);
    }

    @Async
    public void sendOrderStatusUpdate(String toEmail, String customerName, UUID orderId, OrderStatus newStatus) {
        logger.info("A notificar alteração de estado da encomenda #{} para {}", orderId, newStatus);

        String subject = "Atualização da Encomenda #" + orderId.toString().substring(0, 8);
        String bodyDetails;

        switch (newStatus) {
            case PAID -> {
                subject = "Pagamento Confirmado! 🕯️ #" + orderId.toString().substring(0, 8);
                bodyDetails = "Recebemos o teu pagamento. Vamos começar a preparar as tuas velas com todo o cuidado!";
            }
            case SHIPPED -> {
                subject = "A tua encomenda está a caminho! 🚚 #" + orderId.toString().substring(0, 8);
                bodyDetails = "Boas notícias! A tua encomenda foi enviada hoje e deve chegar em breve.";
            }
            case DELIVERED -> {
                subject = "Encomenda Entregue 🏠 #" + orderId.toString().substring(0, 8);
                bodyDetails = "A tua encomenda foi entregue. Esperamos que traga muita luz ao teu refúgio!";
            }
            case CANCELLED -> {
                subject = "Encomenda Cancelada #" + orderId.toString().substring(0, 8);
                bodyDetails = "A tua encomenda foi cancelada. Se tiveres dúvidas, contacta-nos.";
            }
            default -> bodyDetails = "O estado da tua encomenda mudou para: " + newStatus;
        }

        String text = String.format("""
            Olá %s,
            
            %s
            
            Podes ver os detalhes na tua área de cliente.
            
            Obrigado,
            Luz do Refúgio
            """, customerName, bodyDetails);

        emailService.sendEmail(toEmail, subject, text);
    }
}