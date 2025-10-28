package com.notepad.core.config;

import com.notepad.core.interceptor.LoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class LoggingConfig implements WebMvcConfigurer {
    private final LoggingInterceptor loggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                // 에러 발생 시 내부적으로 발생하는 /error 포워딩
                .excludePathPatterns("/error")
                // user registry
                .excludePathPatterns("/api/auth/**")
                // 정적 파일
                .excludePathPatterns("/app/**", "/css/**", "/.well-known/**");
    }
}
