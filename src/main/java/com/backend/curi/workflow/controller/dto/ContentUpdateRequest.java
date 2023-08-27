package com.backend.curi.workflow.controller.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContentUpdateRequest<T> {
    private T content;
}
