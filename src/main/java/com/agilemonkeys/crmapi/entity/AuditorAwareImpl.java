package com.agilemonkeys.crmapi.entity;

import com.agilemonkeys.crmapi.dto.CustomUserPrincipal;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.of(((CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
    }

}
