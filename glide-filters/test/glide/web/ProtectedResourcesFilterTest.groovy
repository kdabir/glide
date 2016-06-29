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

    def "should let request pass through for if block pattern is not provided"() {
        def filter = new ProtectedResourcesFilter()
        filter.init(stubbedFilterConfig('false', null, /.*\.gtpl/))

        HttpServletRequest request = stubbedRequest("/home.groovy")
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
        filter.init(stubbedFilterConfig('false', '/_.*', null))

        HttpServletRequest request = stubbedRequest("/home.groovy")
        HttpServletResponse response = Mock(HttpServletResponse)
        FilterChain chain = Mock(FilterChain)

        when:
        filter.doFilter(request, response, chain)

        then:
        0 * response.sendError(HttpServletResponse.SC_NOT_FOUND)
        1 * chain.doFilter(request, response)
    }

    def "should not let request pass through for blocked patterns"() {
        def filter = new ProtectedResourcesFilter()
        filter.init(stubbedFilterConfig('false', '.*\\.random', '.*\\.gtpl'))

        HttpServletRequest request = stubbedRequest("/home.random")
        HttpServletResponse response = Mock(HttpServletResponse)
        FilterChain chain = Mock(FilterChain)

        when:
        filter.doFilter(request, response, chain)

        then:
        1 * response.sendError(HttpServletResponse.SC_NOT_FOUND)
        0 * chain.doFilter(request, response)
    }

    def "should let request pass through if matched both blocked and except patterns"() {
        def filter = new ProtectedResourcesFilter()
        filter.init(stubbedFilterConfig('false', '/_.*', '/.*\\.html'))

        HttpServletRequest request = stubbedRequest("/_home.html")
        HttpServletResponse response = Mock(HttpServletResponse)
        FilterChain chain = Mock(FilterChain)

        when:
        filter.doFilter(request, response, chain)

        then:
        0 * response.sendError(HttpServletResponse.SC_NOT_FOUND)
        1 * chain.doFilter(request, response)
    }


    def "data based test"() {
        def filter = new ProtectedResourcesFilter()
        filter.init(stubbedFilterConfig(strict, blockPattern, exceptPattern))

        HttpServletRequest request = stubbedRequest(requestUri)
        HttpServletResponse response = Mock(HttpServletResponse)
        FilterChain chain = Mock(FilterChain)

        when:
        filter.doFilter(request, response, chain)

        then:
        ((shouldBeBlocked) ? 1 : 0) * response.sendError(HttpServletResponse.SC_NOT_FOUND)
        ((shouldBeBlocked) ? 0 : 1) * chain.doFilter(request, response)

        where:
        strict  | blockPattern | exceptPattern | requestUri     | shouldBeBlocked
        'false' | null         | null          | "/_anything"   | false
        'false' | "/_.*"       | null          | "/_anything"   | true
        'false' | "/_.*"       | null          | "/s/_anything" | false
        'false' | ".*/_.*"     | null          | "/s/_anything" | true
        'false' | "/_.*"       | null          | "/anything"    | false
        'false' | "/_.*"       | "/_\\d*"      | "/_123"        | false
        'false' | "/_.*"       | "/_\\d*"      | "/_alpha"      | true
        'false' | null         | "/_\\d*"      | "/_123"        | false
        'false' | null         | "/_\\d*"      | "/_alpha"      | false
        'true'  | null         | null          | "/anything"    | true
        'true'  | "/_.*"       | "/_\\d*"      | "/_123"        | true
        'true'  | "/_.*"       | null          | "/_123"        | true
        'true'  | null         | null          | "/_123"        | true
    }


    private FilterConfig stubbedFilterConfig(String strict, String block, String except) {
        FilterConfig filterConfig = Stub(FilterConfig)
        filterConfig.getInitParameter("strict") >> strict
        filterConfig.getInitParameter("except") >> except
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
