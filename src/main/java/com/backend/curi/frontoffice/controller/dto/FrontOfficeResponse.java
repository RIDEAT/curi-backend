package com.backend.curi.frontoffice.controller.dto;

import com.backend.curi.frontoffice.repository.entity.FrontOffice;
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
public class FrontOfficeResponse {
    private UUID id;
    private LaunchedSequenceResponse launchedSequenceResponse;

    public static FrontOfficeResponse of (FrontOffice frontoffice){
        return new FrontOfficeResponse(frontoffice.getId(), LaunchedSequenceResponse.of(frontoffice.getLaunchedSequence()));
    }

}
