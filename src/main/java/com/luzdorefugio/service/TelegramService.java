package com.luzdorefugio.service;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramService {
    @Value("${app.bot.token}")
    private String BOT_TOKEN;

    public void enviarAlertaVenda(String idEncomenda, Double valor, String cliente) {
        String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
        Map<String, Object> body = getStringObjectMap(idEncomenda, valor, cliente);
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject(url, body, String.class);
            System.out.println("✅ Notificação Telegram enviada!");
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar Telegram: " + e.getMessage());
        }
    }

    private @NonNull Map<String, Object> getStringObjectMap(String idEncomenda, Double valor, String cliente) {
        String texto = String.format(
                "🔥 **NOVA VENDA!** 🔥\n\n" +
                        "👤 Cliente: %s\n" +
                        "💰 Valor: %.2f€\n" +
                        "📦 ID: #%s\n\n" +
                        "👉 Vai tratar dela agora!",
                cliente, valor, idEncomenda.substring(0, 8)
        );
        Map<String, Object> body = new HashMap<>();
        String CHAT_ID = "-5151995351";
        body.put("chat_id", CHAT_ID);
        body.put("text", texto);
        body.put("parse_mode", "Markdown"); // Para ficar a negrito
        return body;
    }
}