package com.backend.curi.frontoffice.controller.dto;


import com.backend.curi.workflow.repository.SequenceSatisfactionRepository;
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
    private Boolean isScored;
    public static SequenceSatisfactionResponse of(SequenceSatisfaction satisfaction){
        return new SequenceSatisfactionResponse(satisfaction.getId(), satisfaction.getScore(), satisfaction.getComment(), satisfaction.getIsScored());
    }

    public static SequenceSatisfactionResponse isNone(){
        return new SequenceSatisfactionResponse(0L,0L,"",false);
    }

}
