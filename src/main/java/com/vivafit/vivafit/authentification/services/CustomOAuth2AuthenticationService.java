package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.dto.LoginUserDto;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.exceptions.DataAlreadyExistsException;
import com.vivafit.vivafit.authentification.repositories.UserRepository;
import com.vivafit.vivafit.authentification.responses.LoginResponse;
import com.vivafit.vivafit.manage_calories.services.BMRDetailsService;
import com.vivafit.vivafit.manage_calories.services.MealTypeService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2AuthenticationService extends DefaultOAuth2UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private SignInTokenService signInTokenService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private BMRDetailsService bmrDetailsService;
    @Autowired
    private MealTypeService mealTypeService;

    @Value("${upload.folder.users-photos.path}")
    private String uploadFolderUsersPhotosPath;
    @Value("${upload.folder.users-folders.path}")
    private String uploadFolderUsersFoldersPath;
    @Value("${OAuth2.super.secret.password}")
    private String OAuth2SuperSecretPassword;
    @Value("${facebook.graph.access.token}")
    private String facebookGraphAccessToken;
    @Value("${server.link.login-success-oauth2}")
    private String loginSuccessOAuth2Link;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String email = null;
        String name = null;
        String profilePicture = null;

        if (registrationId.equals("google")){
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
            profilePicture = oAuth2User.getAttribute("picture");
        }
        else{
            if (registrationId.equals("facebook")){
                email = oAuth2User.getAttribute("email");
                name = oAuth2User.getAttribute("name");
                String userId = oAuth2User.getAttribute("id");
                try {
                    profilePicture = getFacebookProfilePicture(userId);
                    System.out.println(profilePicture);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && (user.getCreatedWith().equals("GOOGLE") || user.getCreatedWith().equals("FACEBOOK"))){
            user = loginUserWithOAuth2(user);
            String token = generateJwtTokenForOAuth2(user);

            return oAuth2User;
        }
        else if (user != null && !user.getCreatedWith().equals("GOOGLE") && !user.getCreatedWith().equals("FACEBOOK")){
            throw new OAuth2AuthenticationException(new OAuth2Error("oauth2_auth_failed"), "Email already exists");
        }
        user = userRepository.findByUsername(name).orElse(null);
        if (user != null){
            int i = 1;
            while (user != null){
                user = userRepository.findByUsername(name + i).orElse(null);
                i++;
            }
            name = name + (i-1);
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(name);
        newUser.setPassword(passwordEncoder.encode(OAuth2SuperSecretPassword));
        newUser.setPhoneNumber("+" + name);
        newUser.setProfilePicture(profilePicture);
        newUser.setRole("USER");
        if (registrationId.equals("google")){
            newUser.setCreatedWith("GOOGLE");
        }
        else{
            newUser.setCreatedWith("FACEBOOK");
        }

        String uploadUserFolder = uploadFolderUsersFoldersPath + name;
        Path uploadUserFolderPath = Paths.get(uploadUserFolder);
        if(!Files.exists(uploadUserFolderPath)){
            try {
                Files.createDirectories(uploadUserFolderPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory for users folders", e);
            }
        }

        if(profilePicture != null && !profilePicture.isEmpty()){
            String uploadFolder = uploadFolderUsersPhotosPath;
            Path uploadFolderPath = Paths.get(uploadFolder);
            if(!Files.exists(uploadFolderPath)){
                try {
                    Files.createDirectories(uploadFolderPath);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create directory for users photos", e);
                }
            }
            String profilePicturePath = uploadFolderUsersPhotosPath + name + ".jpg";
            try(InputStream in = new URL(profilePicture).openStream()){
                Files.copy(in, Paths.get(profilePicturePath), StandardCopyOption.REPLACE_EXISTING);
                newUser.setProfilePicture(profilePicturePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save or download profile picture", e);
            }
        }
        else{
            String profilePicturePath = null;
            String defaultProfilePicturePath = uploadFolderUsersPhotosPath + "/default.png";
            File defaultImageFile = new File(defaultProfilePicturePath);
            String extension = ".png";
            String filename = StringUtils.cleanPath(name + extension);
            Path uploadFolderPath = Paths.get(uploadFolderUsersPhotosPath);
            if (!Files.exists(uploadFolderPath)) {
                try {
                    Files.createDirectories(uploadFolderPath);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create directory for users photos", e);
                }
            }
            Path newFilePath = uploadFolderPath.resolve(filename);
            try {
                Files.copy(defaultImageFile.toPath(), newFilePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy default profile picture", e);
            }
            profilePicturePath = newFilePath.toString();
            newUser.setProfilePicture(profilePicturePath);
        }
        userRepository.save(newUser);
        bmrDetailsService.initializeBMRDetails(newUser);
        mealTypeService.initializeMealType(user);

        loginUserWithOAuth2(newUser);
        String token = generateJwtTokenForOAuth2(newUser);

        return oAuth2User;
    }

    public User loginUserWithOAuth2(User user){
        String password = OAuth2SuperSecretPassword;
        String username = user.getUsername();
        Authentication newAuthentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        return user;
    }

    public String generateJwtTokenForOAuth2(User user){
        String existingToken = signInTokenService.getToken(user);
        if (existingToken != null && jwtService.isTokenValid(existingToken, user)) {
            signInTokenService.unregisterToken(user);
            signInTokenService.notifyUserOfDesconnection(user);
        }
        String token = jwtService.generateToken(user);
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(jwtService.getExpirationTime()/1000);
        signInTokenService.registerToken(user, token, expiryDate);
        return token;
    }

    public String getFacebookProfilePicture(String userId) throws IOException {
        String url = "https://graph.facebook.com/" + userId + "/picture?type=large&access_token=" + facebookGraphAccessToken;
        return url;
    }
}
