package com.luzdorefugio.web.admin;

import com.luzdorefugio.dto.admin.EnumResponse;
import com.luzdorefugio.service.EnumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/enums")
@RequiredArgsConstructor
public class EnumController {

    private final EnumService enumService;

    @GetMapping("/materials-type")
    public ResponseEntity<List<EnumResponse>> getMaterialTypes() {
        return ResponseEntity.ok(enumService.getMaterialTypes());
    }

    @GetMapping("/order-status")
    public ResponseEntity<List<EnumResponse>> getOrderStatus() {
        return ResponseEntity.ok(enumService.getOrderStatus());
    }
}