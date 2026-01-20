package com.luzdorefugio.domain;

import com.luzdorefugio.domain.base.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contact_messages")
@Data // Lombok
public class ContactMessage extends Auditable {
    @Id
    @UuidGenerator
    private UUID id;
    private String name;
    private String email;
    @Column(columnDefinition = "TEXT")
    private String message;
    private boolean isRead = false;
}