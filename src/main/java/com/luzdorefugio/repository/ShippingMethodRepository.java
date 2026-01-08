package com.luzdorefugio.repository;

import com.luzdorefugio.domain.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, UUID> {
    List<ShippingMethod> findByActiveTrueOrderByDisplayOrderAsc();

    Optional<ShippingMethod> findByName(String name);

}