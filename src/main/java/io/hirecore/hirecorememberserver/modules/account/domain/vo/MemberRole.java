package io.hirecore.hirecorememberserver.modules.account.domain.vo;

public enum MemberRole {
    ADMIN("내부 관리자"),
    USER("일반 회원"),
    CORPORATOR("기업 회원");

    private final String type;

    MemberRole(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
