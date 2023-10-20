package com.backend.curi.workflow.repository.entity.contents;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AttachmentContent {
    @Builder.Default
    private List<String> extensions = new ArrayList<>();
    private String description;
}
