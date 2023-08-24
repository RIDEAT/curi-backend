package com.backend.curi.frontoffice.repository;

import com.backend.curi.frontoffice.repository.entity.FrontOffice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FrontOfficeRepository extends JpaRepository<FrontOffice, UUID> {
}
