package com.agilemonkeys.crmapi.config;

import com.agilemonkeys.crmapi.entity.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

    @Bean
    AuditorAware<Long> auditorProvider() {
        return new AuditorAwareImpl();
    }

}
