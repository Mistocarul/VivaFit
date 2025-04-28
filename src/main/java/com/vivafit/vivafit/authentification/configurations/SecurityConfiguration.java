package com.vivafit.vivafit.authentification.configurations;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.exceptions.InvalidTokenException;
import com.vivafit.vivafit.authentification.filters.JwtAuthenticationFilter;
import com.vivafit.vivafit.authentification.services.CustomOAuth2AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.vivafit.vivafit.authentification.configurations.CustomOAuth2SuccessHandler;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    @Autowired
    private CustomOAuth2FailureHandler customOAuth2FailureHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { //configureaza securitatea aplicatiei
        http
                .csrf(csrf -> csrf.disable()) //dezactiveaza protectia CSRF
                .cors(withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/contact-us/**").permitAll()
                        .requestMatchers("/register.html", "/login.html", "/confirm.html", "/update.html",
                                "/login-with-google.html", "/login-success-oauth2.html").permitAll()
                        .requestMatchers("/api/account/all-users").hasAuthority("ADMIN")
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/chat/**").permitAll()
                        .requestMatchers("/**").permitAll() // permet accesul la toate celelalte endpoint-uri
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2AuthenticationService()))
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler(customOAuth2FailureHandler)
                )
                .formLogin(formLogin -> formLogin.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //configureaza managementul sesiunilor sa depinda de JWT
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            String errorMessage = authException.getMessage(); // extrage mesajul din excepție
                            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + errorMessage + "\"}");
                        })
                );
        return http.build();
    }

    @Bean
    public CustomOAuth2AuthenticationService oAuth2AuthenticationService() {
        return new CustomOAuth2AuthenticationService();
    }

}
