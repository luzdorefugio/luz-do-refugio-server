package com.luzdorefugio.web.admin;

import com.luzdorefugio.dto.admin.AdjustStockRequest;
import com.luzdorefugio.dto.admin.BulkPurchaseRequest;
import com.luzdorefugio.dto.admin.RestockRequest;
import com.luzdorefugio.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    // Endpoint: POST /api/inventory/adjust
    @PostMapping("/adjust")
    public ResponseEntity<Void> adjustStock(@Valid @RequestBody AdjustStockRequest request) {
        service.reportLoss(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/purchase")
    public ResponseEntity<Void> purchaseMaterial(@RequestBody RestockRequest request) {
        service.registerPurchase(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk-purchase")
    public ResponseEntity<Void> bulkPurchase(@RequestBody BulkPurchaseRequest request) {
        service.registerBulkPurchase(request);
        return ResponseEntity.ok().build();
    }
}