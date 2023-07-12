package com.backend.curi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    WORKSPACE_NOT_EXISTS("WORKSPACE-001", "존재하지 않는 워크 스페이스입니다."),
    DUPLICATED_WORKSPACE_NAME ("WORKSPACE-002", "중복된 워크 스페이스 이름입니다."),
    UNAUTHORIZED_WORKSPACE ("WORKSPACE-003", "해당 워크스페이스에 접근권한이 없습니다."),

    AUTH_SERVER_ERROR ("AUTHSERVER-001", "인증 서버가 정상적으로 동작하지 않습니다. (firebase token, auth token, refresh token 확인요망) "),

    USER_NOT_EXISTS ("USER-001", "존재하지 않는 유저입니다."),
    UNAUTHORIZED_USER ("USER-002","해당 유저에 접근 권한이 없습니다.");


    private final String errorCode;
    private final String message;
}
