package glide.web

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ProtectedResourcesFilterTest extends GroovyTestCase {

    void "test should not proceed in chain if url starts with underscore"() {
        def request = [getRequestURI: {-> "/_protected_resource"}] as HttpServletRequest
        def notFound
        def response = [sendError: { code-> notFound = code}] as HttpServletResponse
        def is_chain_called = false
        def chain = [doFilter: { HttpServletRequest req, HttpServletResponse resp -> is_chain_called = true }] as FilterChain

        new ProtectedResourcesFilter().doFilter(request, response, chain)
        assert notFound == HttpServletResponse.SC_NOT_FOUND
        assert !is_chain_called
    }

    void "test should proceed in chain if url does not starts with underscore"() {
        def request = [getRequestURI: {-> "/allowed_url" },] as HttpServletRequest
        def response = [:] as HttpServletResponse
        def chained = false
        def chain = [doFilter: { HttpServletRequest req, HttpServletResponse resp -> chained = true }] as FilterChain

        new ProtectedResourcesFilter().doFilter(request, response, chain)

        assert chained == true
    }

    void "test should allow _ah urls"() {
        def request = [getRequestURI: {-> "/_ah/anything" },] as HttpServletRequest
        def response = [:] as HttpServletResponse
        def chained = false
        def chain = [doFilter: { HttpServletRequest req, HttpServletResponse resp -> chained = true }] as FilterChain

        new ProtectedResourcesFilter().doFilter(request, response, chain)

        assert chained == true
    }

    void "test strict mode should not allow passing of any request" () {
        def request = [getRequestURI: {-> "/protected_resource"}] as HttpServletRequest
        def notFound
        def response = [sendError: { code-> notFound = code}] as HttpServletResponse
        def is_chain_called = false
        def chain = [doFilter: { HttpServletRequest req, HttpServletResponse resp -> is_chain_called = true }] as FilterChain

        final filter = new ProtectedResourcesFilter()
        filter.strictMode = true
        filter.doFilter(request, response, chain)

        assert notFound == HttpServletResponse.SC_NOT_FOUND
        assert !is_chain_called
    }

}
