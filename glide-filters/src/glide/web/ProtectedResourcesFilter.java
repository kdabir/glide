package glide.web;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Pattern;


/**
 * This filter responds with a 404 for resources that are marked as protected.
 * <p>
 * Resources can be marked as protected by providing a {@code strict} init param.
 * in strict mode <code>strict == true</code>, every client request reaching this filter will be essentially served a 404
 * <p>
 * If <code>strict</code> mode if <code>false</code>,
 * and if the request matches <code>block<code/> pattern, and the client will be served a 404
 * but if the request matches <code>allow<code/> pattern, and the request will pass through.
 *
 * <p>
 * This filter should be applied to only direct requests from client, i.e. dispatcher type REQUEST (which is default)
 * <p>
 * This filter should be the last filter in the filter chain. a routing filter should have routed the request and hence
 * no well-intentioned request should reach this filter
 * <p>
 * Strict mode take preference above all.
 */
public class ProtectedResourcesFilter implements Filter {

    private static final Logger logger = Logger.getLogger(ProtectedResourcesFilter.class.getName());

    private boolean strictMode;
    private Pattern allowPattern = null, blockPattern = null;


    private String initParamOrDefault(FilterConfig config, String key, String defaultValue) {
        String value = config.getInitParameter(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        strictMode = Boolean.parseBoolean(filterConfig.getInitParameter("strict"));

        String blockPatternParam = filterConfig.getInitParameter("block");
        if (blockPatternParam != null) {
            blockPattern = Pattern.compile(blockPatternParam);
        }

        String allowPatternParam = filterConfig.getInitParameter("allow");
        if (allowPatternParam != null) {
            allowPattern = Pattern.compile(allowPatternParam);
        }

        logger.info(String.format("ProtectedResourcesFilter loaded with strict=%b block=%s allow=%s ",
                strictMode, blockPatternParam, allowPatternParam));

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();

        if (strictMode || (isBlocked(requestURI) && !isAllowed(requestURI))) {
            logger.warning(String.format("protected url reached method=%s uri=%s query=%s Returning 404",
                    request.getMethod(), requestURI, request.getQueryString()));

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        filterChain.doFilter(request, response);

    }

    private boolean isAllowed(String requestURI) {
        return allowPattern != null && allowPattern.matcher(requestURI).matches();
    }

    private boolean isBlocked(String requestURI) {
        return blockPattern != null && blockPattern.matcher(requestURI).matches();
    }


    @Override
    public void destroy() {
        logger.info("ProtectedResourcesFilter unloaded");
    }


}
