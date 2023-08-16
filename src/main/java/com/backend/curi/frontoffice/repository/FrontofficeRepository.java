package com.backend.curi.frontoffice.repository;

import com.backend.curi.frontoffice.repository.entity.Frontoffice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FrontofficeRepository extends JpaRepository<Frontoffice, UUID> {
}
