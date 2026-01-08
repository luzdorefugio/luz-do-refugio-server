package com.luzdorefugio.service;

import com.luzdorefugio.domain.enums.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final JavaMailSender mailSender;

    // Define o remetente padrão
    private static final String FROM_EMAIL = "loja@luzdorefugio.pt";

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendLowStockAlert(String materialName, BigDecimal currentQty, BigDecimal minQty) {
        try {
            logger.info("A enviar alerta de stock para: {}", materialName);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo("admin@luzdorefugio.pt");
            message.setSubject("⚠️ ALERTA: Stock Crítico - " + materialName);
            message.setText(String.format("""
                Olá Gestor,
                
                O stock do material '%s' desceu abaixo do nível mínimo!
                
                Quantidade Atual: %s
                Mínimo Definido: %s
                
                Por favor, encomenda mais material.
                
                Cumprimentos,
                Sistema Luz do Refúgio 🕯️
                """, materialName, currentQty, minQty));

            mailSender.send(message);
            logger.info("Email de stock enviado com sucesso!");

        } catch (Exception e) {
            logger.error("Falha ao enviar email de alerta de stock", e);
        }
    }

    @Async
    public void sendOrderConfirmation(String toEmail, String customerName, UUID orderId, BigDecimal total) {
        try {
            if (toEmail == null) return;
            logger.info("A enviar confirmação de encomenda #{} para {}", orderId, toEmail);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(toEmail);
            message.setSubject("Luz do Refúgio - Encomenda Recebida #" + orderId.toString().substring(0, 8));

            message.setText(String.format("""
                Olá %s,
                
                Obrigado pela tua encomenda! ✨
                Recebemos o teu pedido #%s no valor total de %.2f€.
                
                Se escolheste MBWAY ou Transferência, verifica os dados de pagamento na tua área de cliente ou na página de sucesso da compra.
                A tua encomenda será processada assim que confirmarmos o pagamento.
                
                Podes acompanhar o estado aqui: https://luzdorefugio.pt/loja/minha-conta
                
                Com carinho,
                A Equipa Luz do Refúgio
                """, customerName, orderId, total));

            mailSender.send(message);
            logger.info("Email de confirmação enviado para {}", toEmail);
        } catch (Exception e) {
            logger.error("Falha ao enviar confirmação de encomenda para {}", toEmail, e);
        }
    }

    @Async
    public void sendOrderStatusUpdate(String toEmail, String customerName, UUID orderId, OrderStatus newStatus) {
        try {
            logger.info("A notificar alteração de estado da encomenda #{} para {}", orderId, newStatus);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(toEmail);

            String subject = "Atualização da Encomenda #" + orderId.toString().substring(0, 8);
            String bodyDetails = "";

            // Mensagens personalizadas por estado
            switch (newStatus) {
                case OrderStatus.PAID -> {
                    subject = "Pagamento Confirmado! 🕯️ #" + orderId.toString().substring(0, 8);
                    bodyDetails = "Recebemos o teu pagamento. Vamos começar a preparar as tuas velas com todo o cuidado!";
                }
                case OrderStatus.SHIPPED -> {
                    subject = "A tua encomenda está a caminho! 🚚 #" + orderId.toString().substring(0, 8);
                    bodyDetails = "Boas notícias! A tua encomenda foi enviada hoje e deve chegar em breve.";
                }
                case OrderStatus.DELIVERED -> {
                    subject = "Encomenda Entregue 🏠 #" + orderId.toString().substring(0, 8);
                    bodyDetails = "A tua encomenda foi entregue. Esperamos que traga muita luz ao teu refúgio!";
                }
                case OrderStatus.CANCELLED -> {
                    subject = "Encomenda Cancelada #" + orderId.toString().substring(0, 8);
                    bodyDetails = "A tua encomenda foi cancelada. Se tiveres dúvidas, contacta-nos.";
                }
                default -> bodyDetails = "O estado da tua encomenda mudou para: " + newStatus;
            }
            message.setSubject(subject);
            message.setText(String.format("""
                Olá %s,
                
                %s
                
                Podes ver os detalhes na tua área de cliente.
                
                Obrigado,
                Luz do Refúgio
                """, customerName, bodyDetails));
            mailSender.send(message);
            logger.info("Notificação de estado enviada para {}", toEmail);
        } catch (Exception e) {
            logger.error("Falha ao enviar atualização de estado para {}", toEmail, e);
        }
    }
}