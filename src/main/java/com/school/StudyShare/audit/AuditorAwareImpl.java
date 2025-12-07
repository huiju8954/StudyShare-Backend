package com.school.StudyShare.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // 인증정보
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = "";
        if(auth != null) {
            userId = auth.getName();
        }

        return Optional.of(userId);
    }
}
