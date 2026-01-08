package com.luzdorefugio.web.shop;

import com.luzdorefugio.domain.ShippingMethod;
import com.luzdorefugio.service.ShippingMethodService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shop/shipping")
public class ShippingController {
    private final ShippingMethodService service;

    public ShippingController(ShippingMethodService service) {
        this.service = service;
    }

    @GetMapping
    public List<ShippingMethod> getActiveMethods() {
        return service.getActiveMethods();
    }
}