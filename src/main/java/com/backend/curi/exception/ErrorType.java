package com.backend.curi.exception;

import com.backend.curi.notification.repository.entity.Notifications;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    UNEXPECTED_SERVER_ERROR("SERVER-001", "서버 관리자에게 문의하세요."),
    MISSING_REQUIRED_VALUE_ERROR("COMMON-001", "필수 요청값이 누락되었습니다."),
    NOT_ALLOWED_PERMISSION_ERROR("COMMON-002", "허용되지 않은 권한입니다."),
    DUPLICATED_REQUEST_ERROR("COMMON-003", "중복된 요청입니다."),
    INVALID_REQUEST_ERROR("COMMON-004", "올바르지 않은 데이터 요청입니다."),
    ASYNC_HANDLING_ERROR("COMMON-005", "비동기 처리에서 문제가 발생했습니다."),
    NETWORK_ERROR("COMMON-006", "네트워크 처리에서 문제가 발생했습니다."),
    INVALID_URL_ERROR ("COMMON-007", "올바르지 않은 URL입니다."),
    INVALID_DATA_DELETE ("COMMON-008", "데이터 삭제에서 문제가 발생했습니다."),
    CONTENT_PARSE_ERROR("COMMON-009", "컨텐츠 파싱에 실패했습니다."),
    INVALID_FILE_NAME("COMMON-010", "파일 이름 인코딩에 실패했습니다."),


    WORKSPACE_NOT_EXISTS("WORKSPACE-001", "존재하지 않는 워크 스페이스입니다."),
    DUPLICATED_WORKSPACE_NAME("WORKSPACE-002", "중복된 워크 스페이스 이름입니다."),
    UNAUTHORIZED_WORKSPACE("WORKSPACE-003", "해당 워크스페이스에 접근권한이 없습니다."),

    ROLE_NOT_EXISTS("ROLE-001", "존재하지 않는 롤입니다."),
    ROLE_MEMBER_NOT_EXISTS("ROLE-002", "롤에 맞는 직원이 없습니다."),

    MEMBER_NOT_EXISTS("MEMBER-001", "존재하지 않는 직원입니다."),
    EMPLOYEE_AND_MANAGER_NOT_IN_SAME_WORKSPACE("MEMBER-002", "직원과 매니저가 같은 워크스페이스에 속해있지 않습니다."),
    WORKFLOW_NOT_EXISTS("WORKFLOW-001", "존재하지 않는 워크플로우입니다."),
    WORKFLOW_NOT_NORMAL ("WORKFLOW-002", "필수입력값이 누락된 워크플로우입니다."),
    AUTH_SERVER_ERROR ("AUTHSERVER-001", "인증 서버가 정상적으로 동작하지 않습니다. (firebase token, auth token, refresh token 확인요망) "),
    USER_NOT_EXISTS("USER-001", "존재하지 않는 유저입니다."),
    UNAUTHORIZED_USER("USER-002", "해당 유저에 접근 권한이 없습니다."),
    SEQUENCE_MODULE_NOT_EXISTS("MODULE-001", "시퀀스에 해당 모듈이 존재하지 않습니다."),
    MODULE_NOT_EXISTS("MODULE-002", "존재하지 않는 모듈입니다."),
    CONTENT_NOT_EXISTS("MODULE-003", "존재하지 않는 콘텐츠입니다."),
    MODULE_TYPE_NOT_MATCH("MODULE-004", "모듈의 타입이 일치하지 않습니다."),
    INVALID_FILE_EXTENSION("MODULE-005", "올바르지 않은 파일 확장자입니다."),
    SEQUENCE_MODULE_ALREADY_EXISTS("MODULE-004", "시퀀스에 해당 모듈이 이미 존재합니다. 모듈은 중복될 수 없습니다."),
    WORKFLOW_SEQUENCE_NOT_EXISTS("SEQUENCE-001", "워크플로우에 해당 시퀀스가 존재하지 않습니다."),
    SEQUENCE_NOT_EXISTS("SEQUENCE-002", "존재하지 않는 시퀀스 입니다."),
    WORKFLOW_SEQUENCE_ALREADY_EXISTS("SEQUENCE-003", "워크플로우에 해당 시퀀스가 이미 존재합니다. 시퀀스는 중복될 수 없습니다."),
    SEQUENCE_CAN_NOT_SATISFACTION("SEQUENCE-004", "평가할 수 없는 시퀀스입니다."),
    SEQUENCE_ALREADY_SATISFACTION("SEQUENCE-005", "이미 평가를 완료한 시퀀스입니다."),
    ATTACHMENTS_NOT_EXISTS("CONTENT-001", "존재하지 않는 첨부파일입니다."),

    FRONTOFFICE_NOT_EXISTS("FRONTOFFICE-001", "존재하지 않는 프론트오피스입니다."),
    FRONTOFFICE_UNAUTHORIZED ("FRONTOFFICE-002", "프론트오피스 접근권한이 없습니다."),

    SLACK_ACCESS_TOKEN_NOT_EXISTS("SLACK-001", "슬랙의 어세스 토큰이 없습니다."),
    SLACK_MEMBER_NOT_AUTHORIZED("SLACK-002", "슬랙에 인증되지 않은 멤버입니다."),
    SLACK_ADMIN_USER_NOT_AUTHORIZED ("SLACK-003", "슬랙에 인증되지 않은 어드민 유저입니다."),
    SLACK_OAUTH_FAILED("SLACK-004", "슬랙의 OAuth 인증에 실패했습니다."),
    SLACK_OAUTH_ALREADY_EXISTS("SLACK-005", "이미 슬랙에 OAuth 인증이 되어있습니다."),
    SLACK_MESSAGE_FAILED("SLACK-006", "슬랙 메세지 전송에 실패했습니다."),

    INVALID_IMAGE_NAME("COMMON-007", "올바르지 않은 이미지 이름입니다."),
    WORKSPACE_LOGO_ALREADY_EXISTS("WORKSPACE-004", "이미 로고가 존재합니다."),
    WORKSPACE_LOGO_NOT_EXISTS("WORKSPACE-005", "로고가 존재하지 않습니다."),


    NOTIFICATION_NOT_EXISTS("NOTIFICATION-001", "존재하지 않는 알림입니다.");



    private final String errorCode;
    private final String message;
}
