package com.notepad.core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTR = "startTime";
    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTR, startTime);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        long executionTime = System.currentTimeMillis() - startTime;

        String requestId = (String) request.getAttribute(REQUEST_ID);
        Long userId = (Long) request.getAttribute(USER_ID);
        String uri = request.getRequestURI();

        int status = response.getStatus();

        log.info("[requestId:{}] [userId:{}] [uri:{}] [executionTime:{}] [status:{}]", requestId, userId, uri, executionTime, status);
    }
}
