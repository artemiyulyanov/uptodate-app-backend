package me.artemiyulyanov.uptodate.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {
    @Value("${mail.links.registration.url}")
    private String registrationMessageUrl;

    @Value("${mail.links.change-password.url}")
    private String changePasswordMessageUrl;

    @Value("${mail.links.change-email.url}")
    private String changeEmailMessageUrl;

    @Bean(name = "registrationMailMessageUrl")
    public String registrationMailMessageUrl() {
        return registrationMessageUrl;
    }

    @Bean(name = "changePasswordMailMessageUrl")
    public String changePasswordMailMessageUrl() {
        return changePasswordMessageUrl;
    }

    @Bean(name = "changeEmailMailMessageUrl")
    public String changeEmailMailMessageUrl() {
        return changeEmailMessageUrl;
    }
}