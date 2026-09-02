package com.pmpml.location;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean SecurityFilterChain security(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        return http.csrf(csrf -> csrf.disable()).sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a.requestMatchers("/actuator/health/**").permitAll().requestMatchers("/api/v1/buses/*/location").permitAll().anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();
    }
    @Bean JwtFilter jwtFilter(@Value("${app.jwt.secret}") String secret) { return new JwtFilter(secret); }

    static class JwtFilter extends OncePerRequestFilter {
        private final SecretKey key;
        JwtFilter(String secret) { key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)); }
        @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, jakarta.servlet.ServletException {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) try {
                Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(header.substring(7)).getPayload();
                List<?> roles = claims.get("roles", List.class);
                var authorities = roles == null ? List.<SimpleGrantedAuthority>of() : roles.stream().map(String::valueOf).map(SimpleGrantedAuthority::new).toList();
                org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities));
            } catch (RuntimeException ignored) { org.springframework.security.core.context.SecurityContextHolder.clearContext(); }
            chain.doFilter(request, response);
        }
    }
}
