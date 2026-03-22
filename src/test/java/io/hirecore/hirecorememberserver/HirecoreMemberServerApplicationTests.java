package io.hirecore.hirecorememberserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.defer-datasource-initialization=true",
		"spring.data.redis.host=localhost",
		"spring.data.redis.port=6379",
		"hirecore.jwt.secret=test-secret-key-for-unit-testing-only",
		"hirecore.jwt.access-expiration-millis=10800000",
		"hirecore.jwt.refresh-expiration-millis=604800000",
		"hirecore.cookie.http-only=true",
		"hirecore.cookie.secure=false",
		"hirecore.cookie.same-site=Lax",
		"hirecore.cors.allowed-origins=http://localhost:3000",
		"hirecore.cors.allowed-methods=GET",
		"hirecore.cors.allowed-headers=*",
		"hirecore.cors.allow-credentials=true",
		"hirecore.oauth2.kakao.client-id=test-client-id",
		"hirecore.oauth2.kakao.client-secret=test-client-secret",
		"hirecore.oauth2.kakao.grant-type=authorization_code",
		"hirecore.oauth2.kakao.token-issue-url=https://kauth.kakao.com/oauth/token",
		"hirecore.oauth2.kakao.user-profile-url=https://kapi.kakao.com/v2/user/me",
		"hirecore.oauth2.kakao.client-redirect-url=http://localhost:3000/login/oauth/callback/kakao",
})
class HirecoreMemberServerApplicationTests {

	@Test
	void contextLoads() {
	}

}
