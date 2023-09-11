package com.backend.curi.launched.repository.entity;

public enum LaunchedStatus {
    TO_DO {
        @Override
        public String toString() {
            return "대기중";
        }
    },
    IN_PROGRESS {
        @Override
        public String toString() {
            return "진행중";
        }
    },
    OVERDUE {

        @Override
        public String toString() {
            return "기한지남";
        }
    },
    COMPLETED {
        @Override
        public String toString() {
            return "완료됨";
        }
    },
    SKIPPED {
        @Override
        public String toString() {
            return "건너뛰기";
        }
    },
    MARKED_AS_COMPLETED {
        @Override
        public String toString() {
            return "완료로 표시";
        }
    };}
