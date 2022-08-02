package br.com.sgi.handlers;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
        HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse,
        AccessDeniedException e
    ) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        httpServletResponse.getOutputStream().println(
            "{ \"timestamp\": \""
                + LocalDateTime.now()
                + "\", \"error\": \""
                + "Access is denied"
                + "\", \"status\": 403 , \"message\": \""
                + "The server understood the request but refuses to authorize it."
                + "\", \"path\": \""
                + httpServletRequest.getServletPath()
                + "\" }"
        );

    }
}
