package com.luzdorefugio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.api.url}")
    private String apiUrl;

    @Value("${app.email.sender}")
    private String senderEmail;

    public void sendEmail(String toEmail, String subject, String content) {
        if (toEmail.isEmpty()) {
            return;
        }
        try {
            // 1. Headers (Autenticação)
            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // 2. Corpo do Email (JSON)
            Map<String, Object> body = new HashMap<>();

            // Remetente
            Map<String, String> sender = new HashMap<>();
            sender.put("name", "Luz do Refúgio");
            sender.put("email", senderEmail);
            body.put("sender", sender);

            // Destinatário
            Map<String, String> to = new HashMap<>();
            to.put("email", toEmail);
            body.put("to", List.of(to));

            // Assunto e Conteúdo (Convertemos \n para <br> para ficar bonito em HTML)
            body.put("subject", subject);
            String htmlContent = content.replace("\n", "<br>");
            body.put("htmlContent", "<html><body>" + htmlContent + "</body></html>");

            // 3. Envio
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(apiUrl, request, String.class);

            logger.info("✅ Email API enviado para: {}", toEmail);

        } catch (Exception e) {
            logger.error("❌ Erro ao enviar email via API: {}", e.getMessage());
            // Não lançamos erro para não bloquear a compra, mas fica no log
        }
    }
}