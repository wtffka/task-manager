package hexlet.code.filter;

import hexlet.code.utils.JWTHelper;
import hexlet.code.utils.AppConstants;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final RequestMatcher publicUrls;
    private final JWTHelper jwtHelper;

    public JWTAuthorizationFilter(final RequestMatcher publicUrls,
                                  final JWTHelper jwtHelper) {
        this.publicUrls = publicUrls;
        this.jwtHelper = jwtHelper;
    }

    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return publicUrls.matches(request);
    }

    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        if (StringUtils.isBlank(request.getHeader(AUTHORIZATION))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please, log in");
        } else {
            final UsernamePasswordAuthenticationToken authToken = Optional.ofNullable(request.getHeader(AUTHORIZATION))
                    .map(header -> header.replaceFirst("^" + AppConstants.BEARER, ""))
                    .map(String::trim)
                    .map(jwtHelper::verify)
                    .map(claims -> claims.get("email"))
                    .map(Object::toString)
                    .map(this::buildAuthToken)
                    .orElseThrow();

            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
        }
    }

    private UsernamePasswordAuthenticationToken buildAuthToken(final String email) {
        return new UsernamePasswordAuthenticationToken(
                email,
                null,
                AppConstants.DEFAULT_AUTHORITIES
        );
    }
}
