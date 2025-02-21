package com.vivafit.vivafit.authentification.configurations;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.exceptions.InvalidTokenException;
import com.vivafit.vivafit.authentification.repositories.UserRepository;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.authentification.services.SignInTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private SignInTokenService signInTokenService;
    @Autowired
    private UserRepository userRepository;

    @Value("${server.link.login-success-oauth2}")
    private String loginSuccessOAuth2Link;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String redirectUrl = loginSuccessOAuth2Link;
        User currentUser = mapToUserEntity(oAuth2User);

        String existingToken = signInTokenService.getToken(currentUser);

        if (existingToken != null && jwtService.isTokenValid(existingToken, currentUser)) {
            redirectUrl = "http://localhost:4200/autentificare?token=" + existingToken + "&username=" + currentUser.getUsername();
        } else {
            throw new InvalidTokenException("Invalid token");
        }

        System.out.println("User: " + currentUser);
        System.out.println("Redirect URL: " + redirectUrl);

        response.sendRedirect(redirectUrl);
    }

    private User mapToUserEntity(DefaultOAuth2User oAuth2User) {
        User user = userRepository.findByEmail(oAuth2User.getAttribute("email")).orElse(null);
        return user;
    }
}