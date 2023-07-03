package com.backend.curi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    WORKSPACE_NOT_EXISTS("WORKSPACE-001", "존재하지 않는 워크 스페이스입니다."),
    DUPLICATED_WORKSPACE_NAME ("WORKSPACE-002", "중복된 워크 스페이스 이름입니다."),
    USER_NOT_EXISTS ("USER-001", "존재하지 않는 유저입니다.");


    private final String errorCode;
    private final String message;
}
