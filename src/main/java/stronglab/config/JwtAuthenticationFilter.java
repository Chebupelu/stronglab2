package stronglab.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final String JWT_SECRET = "YourSuperSecretKeyForStrongLabApp2026";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println("=== JWT FILTER ===");
        System.out.println("Request URI: " + path);

        if (path.startsWith("/api/auth") ||
                path.startsWith("/api/workouts") ||
                path.startsWith("/api/profile") ||
                path.startsWith("/api/athletes")) {

            System.out.println("Skipping JWT check for: " + path);

            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                try {
                    DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(JWT_SECRET))
                            .build()
                            .verify(token);
                    System.out.println("JWT valid for: " + decodedJWT.getSubject());
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(decodedJWT.getSubject(), null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (Exception e) {
                    System.out.println("JWT invalid: " + e.getMessage());
                }
            }

            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing token");
            return;
        }

        String token = header.substring(7);
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(JWT_SECRET))
                    .build()
                    .verify(token);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(decodedJWT.getSubject(), null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}