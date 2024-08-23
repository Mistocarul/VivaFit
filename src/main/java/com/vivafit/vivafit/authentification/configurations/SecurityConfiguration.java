package com.vivafit.vivafit.authentification.configurations;

import com.vivafit.vivafit.authentification.filters.JwtAuthenticationFilter;
import com.vivafit.vivafit.authentification.services.CustomOAuth2AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { //configureaza securitatea aplicatiei
        http
                .csrf(csrf -> csrf.disable()) //dezactiveaza protectia CSRF
                .cors(withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/register.html", "/login.html", "/confirm.html", "/update.html",
                                "/login-with-google.html").permitAll()
                        .requestMatchers("/api/users/all-users").hasAuthority("ADMIN")
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2AuthenticationService()))
                        .loginPage("http://localhost:9090/login-with-google.html")
                        .defaultSuccessUrl("/update.html", true)
                )
                .formLogin(formLogin -> formLogin.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //configureaza managementul sesiunilor sa depinda de JWT
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CustomOAuth2AuthenticationService oAuth2AuthenticationService() {
        return new CustomOAuth2AuthenticationService();
    }
}
