package glide.web;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Logger;

public class RequestLogFilter implements Filter {

    private static final Logger logger = Logger.getLogger(RequestLogFilter.class.getName());

    private boolean logRequest, logHeaders, logParams, logUser;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        logRequest = Boolean.parseBoolean(filterConfig.getInitParameter("logRequest"));
        logHeaders = Boolean.parseBoolean(filterConfig.getInitParameter("logHeaders"));
        logParams = Boolean.parseBoolean(filterConfig.getInitParameter("logParams"));
        logUser = Boolean.parseBoolean(filterConfig.getInitParameter("logUser"));

        logger.info(String.format(
                "RequestLogFilter loaded with logRequest=%b logHeaders=%b logParams=%b logUser=%b",
                logRequest, logHeaders, logParams, logUser));

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();

        if (logRequest) {
            logger.info(String.format("[request] method=%s uri=%s query=%s",
                    request.getMethod(), uri, request.getQueryString()
            ));
        }

        if (logHeaders) {
            logger.info("[headers]" + headersString(request));
        }

        if (logParams) {
            logger.info("[params]" + paramsString(request));
        }

        if (logUser) {
            logger.info("[user]" + userString(request));
        }

        filterChain.doFilter(request, response);

        logger.info(String.format("Returning back from uri=%s in time=%dms",
                uri, (System.currentTimeMillis() - startTime)));
    }

    private String headersString(HttpServletRequest request) {
        KvBuilder kv = new KvBuilder();
        Enumeration headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement().toString();
            kv.append(headerName, request.getHeader(headerName));
        }

        return kv.toString();
    }

    private String userString(HttpServletRequest request) {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (user != null) {
            KvBuilder kv = new KvBuilder();
            return kv.append("nickname", user.getNickname())
                    .append("userId", user.getUserId())
                    .append("email", user.getEmail())
                    .append("federatedId", user.getFederatedIdentity())
                    .append("authDomain", user.getAuthDomain()).toString();
        } else {
            return " No user info in request";
        }
    }

    private String paramsString(HttpServletRequest request) {
        Map params = request.getParameterMap();
        KvBuilder kv = new KvBuilder();

        for (Object entryObj : params.entrySet()) {
            Map.Entry entry = (Map.Entry) entryObj;
            String key = (String) entry.getKey();
            String value = Arrays.toString((String[]) entry.getValue());
            kv.append(key, value);
        }

        return kv.toString();
    }

    public static class KvBuilder {
        private StringBuilder sb;

        KvBuilder() {
            sb = new StringBuilder();
        }

        KvBuilder append(String key, String value) {
            sb.append(" ").append(key).append("=").append(value);
            return this;
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }

    @Override
    public void destroy() {
        logger.info("LogFilter unloaded");
    }


}
