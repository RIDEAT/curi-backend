package com.backend.curi.dashboard.controller.dto;

import com.backend.curi.launched.controller.dto.LaunchedSequenceResponse;
import com.backend.curi.launched.repository.entity.LaunchedStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMemberResponse {
    private String name;
    private LocalDate startDate;
    private LaunchedStatus launchedStatus;
    private Long progress;
    private Long eNPS;
    private List<LaunchedSequenceResponse> launchedSequences;
}
