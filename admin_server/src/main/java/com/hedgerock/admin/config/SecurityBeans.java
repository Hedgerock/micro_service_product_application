package com.hedgerock.admin.config;

import com.hedgerock.admin.web.client.OauthHttpHeadersProvider;
import jakarta.annotation.Priority;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;
import java.util.Optional;

@Configuration
public class SecurityBeans {

    @Bean
    public OauthHttpHeadersProvider oauthHttpHeadersProvider(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService oAuth2AuthorizedClientService
    ) {
        return new OauthHttpHeadersProvider(
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository,
                        oAuth2AuthorizedClientService
                )
        );
    }

    @Bean
    @Priority(0)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {

        return http
                .securityMatchers(customizer -> customizer
                        .requestMatchers(HttpMethod.POST, "/instances")
                        .requestMatchers(HttpMethod.DELETE, "/instances/*")
                        .requestMatchers("/actuator/**")
                )
                .oauth2ResourceServer(customizer -> customizer.jwt(Customizer.withDefaults()))
                .authorizeHttpRequests(customizer -> customizer
                        .requestMatchers("/instances", "/instances/*").hasAuthority("SCOPE_metrics_server")
                        .requestMatchers("/actuator/**").hasAuthority("SCOPE_metrics")
                        .anyRequest().denyAll()
                )
                .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(CsrfConfigurer::disable)
                .build();
    }

    @Bean
    @Priority(1)
    public SecurityFilterChain uiSecurityFilterChain(HttpSecurity http) throws Exception {

        return http
                .oauth2Client(Customizer.withDefaults())
                .oauth2Login(Customizer.withDefaults())
                .authorizeHttpRequests(customizer -> customizer.anyRequest().authenticated())
                .build();
    }


}
