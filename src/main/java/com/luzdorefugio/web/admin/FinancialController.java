package com.luzdorefugio.web.admin;

import com.luzdorefugio.dto.admin.FinancialTransactionResponse;
import com.luzdorefugio.service.FinancialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/financial")
@RequiredArgsConstructor
public class FinancialController {

    private final FinancialService service;

    @GetMapping
    public ResponseEntity<List<FinancialTransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(service.getAll());
    }

    // Futuro: Podes adicionar endpoints para gráficos aqui
    // @GetMapping("/balance") ...
}