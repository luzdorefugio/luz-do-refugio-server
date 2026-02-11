package com.luzdorefugio.service;

import com.luzdorefugio.domain.ContactMessage;
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
    private final EmailService emailService;
    private final String ADMIN_EMAIL = "admin@luzdorefugio.pt";

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    public void sendContactAdminNotification(ContactMessage message) {
        logger.info("A notificar Admin sobre nova mensagem de contacto de: {}", message.getEmail());

        String subject = "🔔 Nova Mensagem: " + message.getMessage();

        String content = String.format("""
            <p>Olá Gestor,</p>
            <p>Recebeste um novo contacto através do site.</p>
            <br>
            <table style="width: 100%%; border-collapse: collapse; margin-bottom: 20px;">
                <tr><td style="padding: 8px; border-bottom: 1px solid #ddd;"><strong>Nome:</strong></td><td style="padding: 8px; border-bottom: 1px solid #ddd;">%s</td></tr>
                <tr><td style="padding: 8px; border-bottom: 1px solid #ddd;"><strong>Email:</strong></td><td style="padding: 8px; border-bottom: 1px solid #ddd;">%s</td></tr>
            </table>
            <div style="background-color: #fff; padding: 15px; border-left: 4px solid #d4af37; font-style: italic;">
                "%s"
            </div>
            """,
                message.getName(), message.getEmail(),
                message.getMessage()
        );

        // Envia para o Admin
        sendHtmlEmail(ADMIN_EMAIL, subject, content, "Responder agora", "mailto:" + message.getEmail());
    }

    @Async
    public void sendOrderPaidReceipt(String toEmail, String customerName, UUID orderId, BigDecimal total, OrderStatus status) {
        if (toEmail == null) return;

        String shortId = orderId.toString().substring(0, 8);
        logger.info("A enviar recibo ({}) da encomenda #{} para {}", status, shortId, toEmail);

        String linkConta = "https://luzdorefugio.pt/loja/minha-conta";
        String subject = "Recibo da tua compra 🧾 #" + shortId;

        // Texto dinâmico
        String statusMessage;
        String statusColor;

        if (status == OrderStatus.DELIVERED) {
            statusMessage = "✅ <strong>Paga e Entregue</strong><br>Obrigado pela visita à nossa banca! Esperamos que gostes.";
            statusColor = "#27ae60"; // Verde
        } else {
            // PAID
            statusMessage = "✅ <strong>Pagamento Confirmado</strong><br>A tua encomenda será preparada para envio/levantamento brevemente.";
            statusColor = "#2980b9"; // Azul
        }

        String content = String.format("""
            <p>Olá %s,</p>
            <p>Obrigado pela tua compra! ✨</p>
            <p>Este email serve como confirmação de que a encomenda <strong>#%s</strong> (%.2f€) foi registada com sucesso.</p>
            
            <div style="background-color: #f0fdf4; border-left: 4px solid %s; padding: 15px; margin: 20px 0; color: #333;">
                %s
            </div>
            
            <p>Esperamos que os nossos produtos tragam muita luz ao teu espaço!</p>
            """, customerName, shortId, total, statusColor, statusMessage);

        sendHtmlEmail(toEmail, subject, content, "Ver na Área de Cliente", linkConta);
    }

    @Async
    public void sendContactClientConfirmation(String toEmail, String name) {
        if (toEmail == null) return;
        logger.info("A enviar confirmação de receção de contacto para: {}", toEmail);

        String subject = "Recebemos a tua mensagem - Luz do Refúgio";

        String content = String.format("""
            <p>Olá %s,</p>
            <p>Obrigado por entrares em contacto com a <strong>Luz do Refúgio</strong>. ✨</p>
            <p>Recebemos a tua mensagem e vamos analisá-la com todo o carinho. Prometemos ser breves na resposta.</p>
            <p>Entretanto, convidamos-te a espreitar as nossas novidades na loja.</p>
            """, name);

        sendHtmlEmail(toEmail, subject, content, "Visitar Loja", "https://luzdorefugio.pt/loja");
    }

    // ==================================================================================
    // 2. ALERTA DE STOCK (ATUALIZADO)
    // ==================================================================================

    @Async
    public void sendLowStockAlert(String materialName, BigDecimal currentQty, BigDecimal minQty) {
        logger.info("A enviar alerta de stock para: {}", materialName);

        String content = String.format("""
            <p style="color: #c0392b; font-weight: bold;">⚠️ Atenção Gestor,</p>
            <p>O stock do material <strong>'%s'</strong> desceu abaixo do nível mínimo!</p>
            <div style="background-color: #fff; padding: 15px; border-radius: 8px; border: 1px solid #e0e0e0; margin: 20px 0;">
                <p style="margin: 5px 0;">📉 <strong>Quantidade Atual:</strong> %s</p>
                <p style="margin: 5px 0;">🛑 <strong>Mínimo Definido:</strong> %s</p>
            </div>
            <p>Por favor, encomenda mais material para não pararmos a produção.</p>
            """, materialName, currentQty, minQty);

        sendHtmlEmail(ADMIN_EMAIL, "⚠️ ALERTA: Stock Crítico - " + materialName, content, "Ir para o Admin", "https://luzdorefugio.pt/admin");
    }

    // ==================================================================================
    // 3. ENCOMENDAS E ESTADOS (ATUALIZADO)
    // ==================================================================================

    @Async
    public void sendOrderConfirmation(String toEmail, String customerName, UUID orderId, BigDecimal total) {
        if (toEmail == null) return;

        String shortId = orderId.toString().substring(0, 8);
        logger.info("A enviar confirmação de encomenda #{} para {}", shortId, toEmail);

        String linkRastreio = "https://luzdorefugio.pt/loja/rastreio/" + orderId.toString();

        String content = String.format("""
            <p>Olá %s,</p>
            <p>Obrigado pela tua encomenda! Que bom ter-te connosco. ✨</p>
            <p>Recebemos o teu pedido <strong>#%s</strong> no valor total de <strong>%.2f€</strong>.</p>
            <p style="font-size: 0.9em; color: #666;">Se escolheste MBWAY ou Transferência, verifica os dados de pagamento na tua área de cliente ou na página de sucesso da compra.</p>
            <p>A tua encomenda será processada assim que confirmarmos o pagamento.</p>
            """, customerName, shortId, total);

        sendHtmlEmail(toEmail, "Encomenda Recebida #" + shortId, content, "Acompanhar Encomenda", linkRastreio);
    }

    @Async
    public void sendOrderStatusUpdate(String toEmail, String customerName, UUID orderId, OrderStatus newStatus) {
        if (toEmail == null) return;

        String shortId = orderId.toString().substring(0, 8);
        logger.info("A notificar alteração de estado da encomenda #{} para {}", shortId, newStatus);

        String subject;
        String bodyDetails;
        String color = "#d4af37"; // Dourado default

        switch (newStatus) {
            case PAID -> {
                subject = "Pagamento Confirmado! 🕯️ #" + shortId;
                bodyDetails = "Recebemos o teu pagamento. Vamos começar a preparar as tuas velas com todo o cuidado e carinho!";
                color = "#27ae60"; // Verde
            }
            case SHIPPED -> {
                subject = "A tua encomenda está a caminho! 🚚 #" + shortId;
                bodyDetails = "Boas notícias! A tua encomenda foi enviada hoje e deve chegar muito em breve ao teu refúgio.";
                color = "#2980b9"; // Azul
            }
            case DELIVERED -> {
                subject = "Encomenda Entregue 🏠 #" + shortId;
                bodyDetails = "A tua encomenda foi entregue. Esperamos que traga muita luz e serenidade ao teu lar!";
                color = "#27ae60"; // Verde
            }
            case CANCELLED -> {
                subject = "Encomenda Cancelada #" + shortId;
                bodyDetails = "A tua encomenda foi cancelada. Se tiveres dúvidas ou se foi um erro, por favor contacta-nos.";
                color = "#c0392b"; // Vermelho
            }
            default -> {
                subject = "Atualização da Encomenda #" + shortId;
                bodyDetails = "O estado da tua encomenda mudou para: <strong>" + newStatus + "</strong>";
            }
        }

        String content = String.format("""
            <p>Olá %s,</p>
            <div style="border-left: 4px solid %s; padding-left: 15px; margin: 20px 0;">
                <p style="font-size: 1.1em;">%s</p>
            </div>
            <p>Podes ver todos os detalhes na tua área de cliente.</p>
            """, customerName, color, bodyDetails);

        sendHtmlEmail(toEmail, subject, content, "Ver Detalhes", "https://luzdorefugio.pt/loja/minha-conta");
    }

    // ==================================================================================
    // HELPER: CONSTRUTOR DE HTML BONITO 🎨
    // ==================================================================================

    private void sendHtmlEmail(String to, String subject, String bodyContent, String btnText, String btnLink) {
        String htmlTemplate = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Georgia', 'Times New Roman', serif; background-color: #f9f5f0; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; box-shadow: 0 0 10px rgba(0,0,0,0.05); }
                    .header { background-color: #5c0a0a; color: #f9f5f0; padding: 30px; text-align: center; }
                    .header h1 { margin: 0; font-size: 24px; letter-spacing: 2px; }
                    .content { padding: 40px 30px; color: #2a1a1a; line-height: 1.6; font-size: 16px; }
                    .button-container { text-align: center; margin-top: 30px; }
                    .btn { background-color: #d4af37; color: #2a1a1a; padding: 12px 25px; text-decoration: none; font-weight: bold; border-radius: 4px; display: inline-block; }
                    .footer { background-color: #2a1a1a; color: #888; padding: 20px; text-align: center; font-size: 12px; }
                    .footer a { color: #d4af37; text-decoration: none; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>LUZ DO REFÚGIO</h1>
                    </div>
                    <div class="content">
                        %s
                        %s
                    </div>
                    <div class="footer">
                        <p>&copy; 2026 Luz do Refúgio. Feito com paixão em Portugal.</p>
                        <p><a href="https://luzdorefugio.pt">www.luzdorefugio.pt</a></p>
                    </div>
                </div>
            </body>
            </html>
            """,
                bodyContent,
                (btnText != null && btnLink != null) ?
                        String.format("<div class='button-container'><a href='%s' class='btn'>%s</a></div>", btnLink, btnText) : ""
        );

        // Assume que o teu emailService suporta HTML (se usas JavaMailSender, tens de definir 'true' no helper)
        emailService.sendEmail(to, subject, htmlTemplate);
    }
}