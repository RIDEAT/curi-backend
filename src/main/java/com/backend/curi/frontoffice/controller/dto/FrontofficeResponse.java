package com.backend.curi.frontoffice.controller.dto;

import com.backend.curi.frontoffice.repository.entity.Frontoffice;
import com.backend.curi.launched.controller.dto.LaunchedSequenceResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FrontofficeResponse {
    private UUID id;
    private LaunchedSequenceResponse launchedSequenceResponse;

    public static FrontofficeResponse of (Frontoffice frontoffice){
        return new FrontofficeResponse(frontoffice.getId(), LaunchedSequenceResponse.of(frontoffice.getLaunchedSequence()));
    }

}
