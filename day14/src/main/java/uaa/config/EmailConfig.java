package uaa.config;

import com.sendgrid.SendGrid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class EmailConfig {

    private AppProperties appProperties;

    @ConditionalOnProperty(prefix = "uaa.email-provider",name="api-key")
    @Bean
    public SendGrid sendGrid(){
        return new SendGrid(appProperties.getEmailProvider().getApiKey());
    }

}
