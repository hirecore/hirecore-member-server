package io.hirecore.hirecorememberserver.common.web;

public class SwaggerDocs {
    private SwaggerDocs() {}

    public static class Tags {

        public static class Account {
            public static final String AUTH = "인증 (Auth)";
        }

        public static class Profile {
            public static final String USER_PROFILE = "사용자 프로필";
        }

        public static class File {
            public static final String USER_FILE = "사용자 파일";
        }

        public static class Category {
            public static final String JOB_CATEGORY = "직무 카테고리";
        }
    }
}
