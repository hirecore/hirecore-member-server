package io.hirecore.hirecorememberserver;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.TimeZone;

@SpringBootApplication
@ConfigurationPropertiesScan({
		"io.hirecore.hirecorememberserver.common.properties",
		"io.hirecore.hirecorememberserver.common.adapter.in.security.properties",
		"io.hirecore.hirecorememberserver.modules.account.adapter.out.oauth2.kakao.properties",
		"io.hirecore.hirecorememberserver.common.adapter.out.jwt.properties",
})
public class HirecoreMemberServerApplication {

	@PostConstruct
	void setUtcTimeZone() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		SpringApplication.run(HirecoreMemberServerApplication.class, args);
	}

}
