package com.luzdorefugio.repository;

import com.luzdorefugio.domain.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, UUID> {
}