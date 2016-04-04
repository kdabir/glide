package glide.web

import spock.lang.Specification

import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class ProtectedResourcesFilterTest extends Specification {


    def "should send a 404 if strict mode is enabled"() {
        def filter = new ProtectedResourcesFilter()
        filter.init(stubbedFilterConfig('true', 'whatever', 'whatever'))

        HttpServletRequest request = stubbedRequest("/doesnt/matter")
        HttpServletResponse response = Mock(HttpServletResponse)
        FilterChain chain = Mock(FilterChain)

        when:
        filter.doFilter(request, response, chain)

        then:
        1 * response.sendError(HttpServletResponse.SC_NOT_FOUND)
        0 * chain.doFilter(request, response)
    }

    def "should let request pass through for allowed patterns"() {
        def filter = new ProtectedResourcesFilter()
        filter.init(stubbedFilterConfig('false', /.*\.gtpl/, '.*'))

        HttpServletRequest request = stubbedRequest("/home.gtpl")
        HttpServletResponse response = Mock(HttpServletResponse)
        FilterChain chain = Mock(FilterChain)

        when:
        filter.doFilter(request, response, chain)

        then:
        0 * response.sendError(HttpServletResponse.SC_NOT_FOUND)
        1 * chain.doFilter(request, response)
    }

    def "should let request pass through if does not match blocked pattern"() {
        def filter = new ProtectedResourcesFilter()
        filter.init(stubbedFilterConfig('false', null, '/_.*'))

        HttpServletRequest request = stubbedRequest("/home.gtpl")
        HttpServletResponse response = Mock(HttpServletResponse)
        FilterChain chain = Mock(FilterChain)

        when:
        filter.doFilter(request, response, chain)

        then:
        0 * response.sendError(HttpServletResponse.SC_NOT_FOUND)
        1 * chain.doFilter(request, response)
    }

    def "should not let request pass thru for blocked patterns"() {
        def filter = new ProtectedResourcesFilter()
        filter.init(stubbedFilterConfig('false', '.*\\.gtpl', '.*\\.random'))

        HttpServletRequest request = stubbedRequest("/home.random")
        HttpServletResponse response = Mock(HttpServletResponse)
        FilterChain chain = Mock(FilterChain)

        when:
        filter.doFilter(request, response, chain)

        then:
        1 * response.sendError(HttpServletResponse.SC_NOT_FOUND)
        0 * chain.doFilter(request, response)
    }


    private FilterConfig stubbedFilterConfig(String strict, String allow, String block) {
        FilterConfig filterConfig = Stub(FilterConfig)
        filterConfig.getInitParameter("strict") >> strict
        filterConfig.getInitParameter("allow") >> allow
        filterConfig.getInitParameter("block") >> block

        return filterConfig
    }

    private stubbedRequest(String uri) {
        HttpServletRequest request = Stub()
        request.getMethod() >> "GET"
        request.getRequestURI() >> uri
        return request
    }

}
