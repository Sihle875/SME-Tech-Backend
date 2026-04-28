package sme.tech.innovators.sme.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import sme.tech.innovators.sme.dto.response.ApiResponse;
import sme.tech.innovators.sme.exception.ErrorCodes;
import sme.tech.innovators.sme.exception.RateLimitExceededException;
import sme.tech.innovators.sme.service.RateLimitService;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String ip = extractIp(request);
        String email = request.getParameter("email");

        try {
            rateLimitService.checkRateLimit(ip, email);
            return true;
        } catch (RateLimitExceededException ex) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ApiResponse<Void> body = ApiResponse.error(ErrorCodes.RATE_LIMIT_EXCEEDED, ex.getMessage());
            response.getWriter().write(objectMapper.writeValueAsString(body));
            return false;
        }
    }

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
