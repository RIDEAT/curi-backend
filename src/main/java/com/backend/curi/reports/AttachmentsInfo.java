package com.backend.curi.reports;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AttachmentsInfo {
    private String fileName;
    @Builder.Default
    private List<String> extensions = new ArrayList<>();
}
