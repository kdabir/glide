package glide.web

import javax.servlet.http.HttpServletRequest
import groovyx.gaelyk.GaelykBindings

import javax.servlet.*
import com.google.appengine.api.users.UserService
import com.google.appengine.api.users.User
import com.google.appengine.api.users.UserServiceFactory

@GaelykBindings
public class GlideLogFilter implements Filter {
    def log = logger['glide']

    def filterConfig
    boolean logStats = false

    @Override
    public void init(FilterConfig config) throws ServletException {
        log.info "Initializing GlideFilter ..."
        filterConfig = config
        logStats = config.getInitParameter('logStats')?.toBoolean()
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response   ,
                         FilterChain chain) throws IOException, ServletException {

        long startTime = System.currentTimeMillis();

        if (logStats) {
            logUrlStats(request, startTime)
        }
        chain.doFilter(request, response)

        if (logStats){
            log.info "[${startTime}] returning back from ${request.requestURI} in [${System.currentTimeMillis() - startTime}]"
        }
    }

    @Override
    public void destroy() {
        log.info "glide app undeployed"
    }

    private void logUrlStats(HttpServletRequest request, tstamp) {

        log.info """[$tstamp]  -- Request Info --
        request.requestURI:         ${request.requestURI}
        request.servletPath:        ${request.servletPath}
        request.pathInfo:           ${request.pathInfo}
        """.toString()

        StringBuilder requestParamBuilder = new StringBuilder()
        requestParamBuilder.append( "[$tstamp]  -- Request Params --\n")
        request.parameterMap.each{ k,v->
            requestParamBuilder.append("\t$k: \t$v\n")
        }
        log.info requestParamBuilder.toString()

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (user != null) {
            log.info """[$tstamp]  -- User Info --
            nickname :      ${user.getNickname()}
            userid :        ${user.getUserId()}
            email :         ${user.getEmail()}
            fed id :        ${user.getFederatedIdentity()}
            auth domain :   ${user.getAuthDomain()}
            """.toString()
        } else {
            log.info("user not found");
        }

    }
}
