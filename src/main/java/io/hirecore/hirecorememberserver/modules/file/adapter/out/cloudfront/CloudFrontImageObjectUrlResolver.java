package io.hirecore.hirecorememberserver.modules.file.adapter.out.cloudfront;

import io.hirecore.hirecorememberserver.modules.file.adapter.out.cloudfront.properties.CloudFrontProperties;
import io.hirecore.hirecorememberserver.modules.file.application.port.out.ResolveImageObjectUrlPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CloudFrontImageObjectUrlResolver implements ResolveImageObjectUrlPort {

    private final CloudFrontProperties cloudFrontProperties;

    @Override
    public String resolveUrl(String objectKey) {
        String base = cloudFrontProperties.publicBaseUrl();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        String key = objectKey.startsWith("/") ? objectKey.substring(1) : objectKey;
        return base + "/" + key;
    }
}
