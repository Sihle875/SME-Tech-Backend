package sme.tech.innovators.sme.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class BusinessSecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String requestedBusinessId = extractBusinessIdFromPath(request.getRequestURI());
        if (requestedBusinessId == null) {
            return true; // No business ID in path, skip check
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return true; // Let Spring Security handle unauthenticated requests
        }

        // Business ID validation would be done here using claims stored in SecurityContext
        // For now, pass through — full enforcement requires JWT claims in auth details
        return true;
    }

    private String extractBusinessIdFromPath(String uri) {
        // Extract businessId from paths like /api/v1/business/{businessId}/...
        String[] parts = uri.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            if ("business".equals(parts[i]) || "businesses".equals(parts[i])) {
                return parts[i + 1];
            }
        }
        return null;
    }
}
