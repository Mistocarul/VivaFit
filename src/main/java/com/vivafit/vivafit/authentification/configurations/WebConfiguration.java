package com.vivafit.vivafit.authentification.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class WebConfiguration {
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("*"));
        //corsConfiguration.setAllowedOrigins(List.of("http://localhost:2020")); //de unde vin requesturile
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")); //ce metode sunt permise
        corsConfiguration.setAllowedHeaders(List.of("*")); //ce antete sunt permise
        corsConfiguration.setAllowCredentials(true); //permite cookie-urile
        corsConfiguration.setMaxAge(3600L); //durata in secunde in care raspunsurile CORS sunt considerate valide
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); //configurare bazata pe URL
        source.registerCorsConfiguration("/**", corsConfiguration); //configurare pentru toate URL-urile
        return source;
    }
}
