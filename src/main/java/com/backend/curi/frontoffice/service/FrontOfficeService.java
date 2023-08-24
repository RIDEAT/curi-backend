package com.backend.curi.frontoffice.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.frontoffice.controller.dto.FrontofficeResponse;
import com.backend.curi.frontoffice.repository.FrontOfficeRepository;
import com.backend.curi.frontoffice.repository.entity.FrontOffice;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor

public class FrontOfficeService {

    private final FrontOfficeRepository frontOfficeRepository;

    public FrontofficeResponse getFrontOffice(UUID frontOfficeId) {
        FrontOffice frontOffice = frontOfficeRepository.findById(frontOfficeId).orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.FRONTOFFICE_NOT_EXISTS));
        return FrontofficeResponse.of(frontOffice);

    }

    public void checkAuth(UUID frontOfficeId, UUID accessToken) {
        FrontOffice frontOffice = frontOfficeRepository.findById(frontOfficeId).orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.FRONTOFFICE_NOT_EXISTS));
        if (!frontOffice.getAccessToken().equals(accessToken)) throw new CuriException(HttpStatus.UNAUTHORIZED, ErrorType.FRONTOFFICE_UNAUTHORIZED);

    }

    public void createFrontOffice(LaunchedSequence launchedSequence) {
        FrontOffice frontOffice = new FrontOffice();
        frontOffice.setLaunchedSequence(launchedSequence);
        frontOffice.setAccessToken(UUID.randomUUID());
        frontOfficeRepository.save(frontOffice);

    }
}
