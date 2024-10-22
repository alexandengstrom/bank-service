package hello;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import se.liu.ida.tdp024.account.logic.utils.KafkaLogger;

@Component
public class RequestLogger implements HandlerInterceptor {

    private final KafkaLogger logger = new KafkaLogger("account-api");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String logMessage = String.format(
            "{\"url\": \"%s\", \"method\": \"%s\", \"remoteAddr\": \"%s\"}", 
            request.getRequestURI(),
            request.getMethod(),
            request.getRemoteAddr()
        );

        this.logger.publish(logMessage);
        return true;
    }

}
