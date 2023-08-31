package com.backend.curi.frontoffice.controller.dto;


import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workflow.repository.entity.SequenceSatisfaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SequenceSatisfactionResponse {
    private Long id;
    private Long score;
    private String comment;

    public static SequenceSatisfactionResponse of(SequenceSatisfaction satisfaction){
        return new SequenceSatisfactionResponse(satisfaction.getId(), satisfaction.getScore(), satisfaction.getComment());
    }
}
