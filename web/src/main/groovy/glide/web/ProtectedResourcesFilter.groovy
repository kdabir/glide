package glide.web

import com.google.appengine.api.users.User
import com.google.appengine.api.users.UserService
import com.google.appengine.api.users.UserServiceFactory
import groovyx.gaelyk.GaelykBindings

import javax.servlet.http.HttpServletRequest
import javax.servlet.*
import javax.servlet.http.HttpServletResponse

@GaelykBindings
public class ProtectedResourcesFilter implements javax.servlet.Filter {
    def log = logger['glide']

    @Override
    public void init(FilterConfig config) throws ServletException {
        log.info "Initializing GlideFilter ..."
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response   ,
                         FilterChain chain) throws IOException, ServletException {

        log.warning "Reached protected resource filter"

        // if this filter is reached we need to block this request!
        boolean startWithUnderscore = request.requestURI.split("/").any { it.startsWith("_") }
        if(!startWithUnderscore || request.requestURI.startsWith('/_ah')){
          chain.doFilter(request, response)
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    public void destroy() {
        log.info "glide app undeployed"
    }

}
