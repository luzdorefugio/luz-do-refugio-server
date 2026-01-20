package com.luzdorefugio.service;

import com.luzdorefugio.domain.ContactMessage;
import com.luzdorefugio.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactService {
    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);
    private final ContactMessageRepository repository;
    private final NotificationService notificationService; // Aquele que cria o email

    @Transactional // Garante que tudo acontece ou nada acontece
    public ContactMessage createContact(ContactMessage message) {
        ContactMessage saved = repository.save(message);
        try {
            //notificationService.sendAdminNotification(saved);
            //notificationService.sendClientConfirmation(saved.getEmail(), saved.getName());
        } catch (Exception e) {
            logger.error("Aviso: Falha ao enviar email de contacto: {}", e.getMessage());
        }

        return saved;
    }

    public List<ContactMessage> getAllMessages() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public void markAsRead(UUID id) {
        repository.findById(id).ifPresent(msg -> {
            msg.setRead(true);
            repository.save(msg);
        });
    }
}