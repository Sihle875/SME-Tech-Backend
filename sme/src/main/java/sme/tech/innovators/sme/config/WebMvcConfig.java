package sme.tech.innovators.sme.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sme.tech.innovators.sme.security.BusinessSecurityInterceptor;
import sme.tech.innovators.sme.security.RateLimitInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;
    private final BusinessSecurityInterceptor businessSecurityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/v1/auth/register", "/api/v1/auth/resend-verification");

        registry.addInterceptor(businessSecurityInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns("/api/v1/auth/**", "/api/v1/public/**");
    }
}
