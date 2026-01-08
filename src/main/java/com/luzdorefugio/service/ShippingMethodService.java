package com.luzdorefugio.service;

import com.luzdorefugio.domain.ShippingMethod;
import com.luzdorefugio.repository.ShippingMethodRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ShippingMethodService {

    private final ShippingMethodRepository repository;

    public ShippingMethodService(ShippingMethodRepository repository) {
        this.repository = repository;
    }

    // --- MÉTODOS PÚBLICOS (LOJA) ---

    /**
     * Retorna apenas os métodos ativos e ordenados.
     * Usado no Checkout.
     */
    public List<ShippingMethod> getActiveMethods() {
        return repository.findByActiveTrueOrderByDisplayOrderAsc();
    }

    public List<ShippingMethod> getAllMethods() {
        // Ordena pela ordem de exibição, mesmo no admin
        return repository.findAll(Sort.by(Sort.Direction.ASC, "displayOrder"));
    }

    public ShippingMethod getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Método de envio não encontrado: " + id));
    }

    public ShippingMethod create(ShippingMethod method) {
        // Podes adicionar lógica aqui, ex: impedir nomes duplicados
        return repository.save(method);
    }

    public ShippingMethod update(UUID id, ShippingMethod updatedData) {
        ShippingMethod existing = getById(id);

        existing.setName(updatedData.getName());
        existing.setDescription(updatedData.getDescription());
        existing.setPrice(updatedData.getPrice());
        existing.setActive(updatedData.isActive());
        existing.setDisplayOrder(updatedData.getDisplayOrder());

        return repository.save(existing);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }


    public void toggleActive(UUID id) {
        ShippingMethod method = getById(id);
        method.setActive(!method.isActive());
        repository.save(method);
    }
}