package glide.web;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Pattern;


/**
 * This filter responds with a 404 for requests for resources that are marked as protected.
 * <p>
 * All resources can be marked as protected by providing a {@code strict} init param.
 * in strict mode, <code>strict == true</code>, every client request reaching this filter will be essentially served a 404
 * <p>
 * If <code>strict</code> mode is <code>false</code>,
 * and if the request matches <code>block<code/> pattern, and the client will be served a 404
 * BUT if the request also matches <code>except<code/> pattern, then the request will pass through.
 *
 * <p>
 * This filter should be applied to only direct requests from client, i.e. dispatcher type <code>REQUEST</code> (which is default)
 * <p>
 * This filter should be the last filter in the filter chain. A routing filter should have routed the request already
 * and hence no well-intentioned request should reach this filter
 * <p>
 * Strict mode takes preference above all and in strict mode, <code>block</code> / <code>except</code>  values will be simply ignored
 * <p>
 * In non strict mode, without providing <code>block</code> value, providing <code>except</code> value does not
 * make sense as every url will be  allowed (hence <code>except</code> value is ignored)
 *
 *
 */
public class ProtectedResourcesFilter implements Filter {

    private static final Logger logger = Logger.getLogger(ProtectedResourcesFilter.class.getName());

    private boolean strictMode;
    private Pattern blockPattern = null, exceptPattern = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        strictMode = Boolean.parseBoolean(filterConfig.getInitParameter("strict"));

        String blockPatternParam = "ignored", exceptPatternParam = "ignored"; // just for better logging

        // nesting this logic saves setting of unnecessary patterns

        // if it's strict mode, we need not check anything else
        if (!strictMode) {
            blockPatternParam = filterConfig.getInitParameter("block");

            // if its not strict and block is null, then there is no point of checking except, as every url will be allowed
            if (blockPatternParam != null) {
                blockPattern = Pattern.compile(blockPatternParam);

                exceptPatternParam = filterConfig.getInitParameter("except");
                if (exceptPatternParam != null) {
                    exceptPattern = Pattern.compile(exceptPatternParam);
                }
            }
        }

        logger.info(String.format("ProtectedResourcesFilter loaded with effective config strict=%b block=%s except=%s ",
                strictMode, blockPatternParam, exceptPatternParam));

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();

        if (strictMode || (isBlocked(requestURI) && !isExempted(requestURI))) {
            logger.warning(String.format("request for protected resource received with method=%s uri=%s query=%s; returning 404",
                    request.getMethod(), requestURI, request.getQueryString()));

            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        filterChain.doFilter(request, response);

    }

    private boolean isBlocked(String requestURI) {
        return blockPattern != null && blockPattern.matcher(requestURI).matches();
    }

    private boolean isExempted(String requestURI) {
        return exceptPattern != null && exceptPattern.matcher(requestURI).matches();
    }

    @Override
    public void destroy() {
        logger.info("ProtectedResourcesFilter unloaded");
    }


}
