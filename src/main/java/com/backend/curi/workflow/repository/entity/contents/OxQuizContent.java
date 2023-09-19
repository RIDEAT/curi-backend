package com.backend.curi.workflow.repository.entity.contents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OxQuizContent {
    private List<String> question;
    private List<String> answer;

}
