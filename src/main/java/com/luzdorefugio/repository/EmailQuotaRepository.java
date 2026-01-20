package com.luzdorefugio.repository;

import com.luzdorefugio.domain.EmailQuota;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailQuotaRepository extends JpaRepository<EmailQuota, String> {
}