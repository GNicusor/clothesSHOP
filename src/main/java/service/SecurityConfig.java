package service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/**",
                                "/index.html",
                                "/manifest.json",
                                "/favicon.png",
                                "/flutter.js",
                                "/main.dart.js",
                                "/assets/**",
                                "/icons/**",
                                "/notification.css"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
