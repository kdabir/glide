package glide.web

import groovyx.gaelyk.GaelykBindings

import javax.servlet.http.HttpServletResponse
import javax.servlet.*

@GaelykBindings
public class ProtectedResourcesFilter implements javax.servlet.Filter {
    def log = logger['glide']

    @Override
    public void init(FilterConfig config) throws ServletException {
        log.info "Initializing ProtectedResourceFilter ..."
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response   ,
                         FilterChain chain) throws IOException, ServletException {

        log.info "Reached protected resource filter"

        boolean startWithUnderscore = request.requestURI.split("/").any { it.startsWith("_") }

        // if this filter is reached for _* url, we need to block this request!
        if(startWithUnderscore && !request.requestURI.startsWith('/_ah')){
            log.warning "trying to access protected reource, returning NOT_FOUND"
            response.sendError(HttpServletResponse.SC_NOT_FOUND)
            return
        }

        chain.doFilter(request, response)
    }

    @Override
    public void destroy() {
        log.info "glide app undeployed"
    }

}
