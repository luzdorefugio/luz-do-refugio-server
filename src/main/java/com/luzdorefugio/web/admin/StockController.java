package com.luzdorefugio.web.admin;

import com.luzdorefugio.dto.admin.InboundRequest;
import com.luzdorefugio.dto.admin.StockMovementResponse;
import com.luzdorefugio.repository.StockMovementRepository;
import com.luzdorefugio.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final InventoryService inventoryService;
    private final StockMovementRepository movementRepo;

    public StockController(InventoryService inventoryService, StockMovementRepository movementRepo) {
        this.inventoryService = inventoryService;
        this.movementRepo = movementRepo;
    }

    @PostMapping("/inbound")
    public ResponseEntity<Void> addStock(@Valid @RequestBody InboundRequest request) {
        inventoryService.processInbound(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/movements")
    public ResponseEntity<List<StockMovementResponse>> getHistory() {
        var history = movementRepo.findTop20ByOrderByTimestampDesc().stream()
                .map(m -> new StockMovementResponse(
                        m.getId(),
                        m.getMaterial().getName(), // Lazy loading pode disparar aqui, mas com top 20 é ok
                        m.getType(),
                        m.getQuantity(),
                        m.getReferenceId(),
                        m.getNotes(),
                        m.getTimestamp()
                ))
                .toList();
        return ResponseEntity.ok(history);
    }
}