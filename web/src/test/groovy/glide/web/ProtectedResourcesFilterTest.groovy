package glide.web

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ProtectedResourcesFilterTest extends GroovyTestCase {

    void testShouldNotProceedInChainIfUrlStartsWithUnderscore() {
        def request = [getRequestURI: {-> "/_protected_resource"}] as HttpServletRequest
        def notFound
        def response = [sendError: { code-> notFound = code}] as HttpServletResponse
        def chained = false
        def chain = [doFilter: { HttpServletRequest req, HttpServletResponse resp -> chained = true }] as FilterChain

        new ProtectedResourcesFilter().doFilter(request, response, chain)
        assert notFound == HttpServletResponse.SC_NOT_FOUND
        assert chained == false
    }

    void testShouldNotProceedInChainIfUrlDoesNotStartsWithUnderscore() {
        def request = [getRequestURI: {-> "/allowed_url" },] as HttpServletRequest
        def response = [:] as HttpServletResponse
        def chained = false
        def chain = [doFilter: { HttpServletRequest req, HttpServletResponse resp -> chained = true }] as FilterChain

        new ProtectedResourcesFilter().doFilter(request, response, chain)

        assert chained == true
    }

    void testShouldAllow_ahUrls() {
        def request = [getRequestURI: {-> "/_ah/anything" },] as HttpServletRequest
        def response = [:] as HttpServletResponse
        def chained = false
        def chain = [doFilter: { HttpServletRequest req, HttpServletResponse resp -> chained = true }] as FilterChain

        new ProtectedResourcesFilter().doFilter(request, response, chain)

        assert chained == true
    }


}
