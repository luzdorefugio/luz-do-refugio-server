package com.luzdorefugio.web.admin;

import com.luzdorefugio.dto.admin.DashboardStatsResponse;
import com.luzdorefugio.dto.admin.SalesByChannelDTO;
import com.luzdorefugio.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        // Delega a lógica para o serviço
        DashboardStatsResponse stats = dashboardService.getStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/sales-by-channel")
    public ResponseEntity<List<SalesByChannelDTO>> getSalesByChannel() {
        // Podes fazer direto aqui ou passar pelo Service se preferires
        return ResponseEntity.ok(dashboardService.getSalesByChannel());
    }
}