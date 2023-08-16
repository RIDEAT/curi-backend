package com.backend.curi.frontoffice.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.frontoffice.controller.dto.FrontofficeResponse;
import com.backend.curi.frontoffice.repository.FrontofficeRepository;
import com.backend.curi.frontoffice.repository.entity.Frontoffice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor

public class FrontofficeService {

    private final FrontofficeRepository frontofficeRepository;

    public FrontofficeResponse getFrontoffice(UUID frontofficeId) {
        Frontoffice frontoffice = frontofficeRepository.findById(frontofficeId).orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.FRONTOFFICE_NOT_EXISTS));
        return FrontofficeResponse.of(frontoffice);

    }

    public void checkAuth(UUID frontofficeId, UUID accessToken) {
        Frontoffice frontoffice = frontofficeRepository.findById(frontofficeId).orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.FRONTOFFICE_NOT_EXISTS));
        if (!frontoffice.getAccessToken().equals(accessToken)) throw new CuriException(HttpStatus.UNAUTHORIZED, ErrorType.FRONTOFFICE_UNAUTHORIZED);

    }
}
