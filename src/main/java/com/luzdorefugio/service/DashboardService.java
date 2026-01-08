package com.luzdorefugio.service;

import com.luzdorefugio.dto.admin.DashboardStatsResponse;
import com.luzdorefugio.dto.admin.SalesByChannelDTO;
import com.luzdorefugio.repository.MaterialRepository;
import com.luzdorefugio.repository.OrderRepository;
import com.luzdorefugio.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final MaterialRepository materialRepo;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        BigDecimal investedMaterials = materialRepo.getTotalInvested();
        BigDecimal potentialRevenue = productRepo.getTotalStockRevenue();
        BigDecimal estimatedProfit = productRepo.getTotalEstimatedProfit();
        return DashboardStatsResponse.builder()
                .totalInvestedMaterials(investedMaterials)
                .potentialRevenue(potentialRevenue)
                .estimatedProfit(estimatedProfit)
                .build();
    }

    @Transactional(readOnly = true)
    public List<SalesByChannelDTO> getSalesByChannel() {
        return orderRepo.getSalesByChannel();
    }
}