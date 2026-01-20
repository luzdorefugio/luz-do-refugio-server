package com.luzdorefugio.web;

import com.luzdorefugio.domain.ContactMessage;
import com.luzdorefugio.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    @PostMapping("/shop/contact")
    public ResponseEntity<ContactMessage> submitContact(@RequestBody ContactMessage payload) {
        ContactMessage saved = contactService.createContact(payload);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/admin/contact")
    public ResponseEntity<List<ContactMessage>> getAllContacts() {
        return ResponseEntity.ok(contactService.getAllMessages());
    }

    @PutMapping("/admin/contact/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        contactService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}
