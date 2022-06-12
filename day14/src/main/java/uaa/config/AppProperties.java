package uaa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Min;

@Configuration
@ConfigurationProperties(prefix = "lxh")
public class AppProperties {

    @Setter
    @Getter
    private Jwt jwt = new Jwt();

    @Getter
    @Setter
    public static class Jwt{
        private String header = "Authorization";
        private String prefix = "Bearer";

        @Min(5000L)
        private long accessTokenExpireTime = 60 * 1000L; // Access Token 过期时间

        @Min(3600000L)
        private long refreshTokenExpireTime = 30 * 24 * 3600 * 1000L; // Refresh Token 过期时间
    }
}
