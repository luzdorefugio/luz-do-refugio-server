package com.luzdorefugio.service;

import com.luzdorefugio.domain.ContactMessage;
import com.luzdorefugio.dto.admin.ContactResponse;
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
    public ContactResponse createContact(ContactMessage message) {
        ContactMessage saved = repository.save(message);
        try {
            notificationService.sendContactAdminNotification(saved);
            notificationService.sendContactClientConfirmation(saved.getEmail(), saved.getName());
        } catch (Exception e) {
            logger.error("Aviso: Falha ao enviar email de contacto: {}", e.getMessage());
        }
        return mapToResponse(saved);
    }

    public List<ContactResponse> getAllMessages() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void markAsRead(UUID id) {
        repository.findById(id).ifPresent(msg -> {
            msg.setRead(true);
            repository.save(msg);
        });
    }

    private ContactResponse mapToResponse(ContactMessage cm) {
        return new ContactResponse(
                cm.getId(),
                cm.getName(),
                cm.getEmail(),
                cm.getMessage(),
                cm.isRead(),
                cm.getCreatedBy(),
                cm.getCreatedAt()
        );
    }
}