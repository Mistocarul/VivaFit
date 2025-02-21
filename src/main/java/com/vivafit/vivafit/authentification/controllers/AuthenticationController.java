package com.vivafit.vivafit.authentification.controllers;

import com.vivafit.vivafit.authentification.dto.*;
import com.vivafit.vivafit.authentification.entities.ConnectionDetails;
import com.vivafit.vivafit.authentification.entities.PendingSignInUser;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.exceptions.InvalidTokenException;
import com.vivafit.vivafit.authentification.repositories.UserRepository;
import com.vivafit.vivafit.authentification.responses.GeneralApiResponse;
import com.vivafit.vivafit.authentification.responses.LoginResponse;
import com.vivafit.vivafit.authentification.responses.RegisterResponse;
import com.vivafit.vivafit.authentification.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/api/auth")
@RestController
@Validated
public class AuthenticationController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private SignInTokenService signInTokenService;
    @Autowired
    private ConnectionDetailsService connectionDetailsService;
    @Autowired
    private ConfirmationAuthService confirmationAuthService;

    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> register(@Valid @ModelAttribute RegisterUserDto registerUserDto) {
        System.out.println(registerUserDto.getProfilePicture());

        User user = authenticationService.registerUser(registerUserDto);
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setMessage("Confirmation email sent. Please check your email for further instructions.");
        registerResponse.setUser(user);
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/confirm")
    public ResponseEntity<RegisterResponse> confirm(@Valid @RequestBody ConfirmationCodeDto confirmationCodeDto) {
        String username = confirmationCodeDto.getUsername();
        int code = confirmationCodeDto.getCode();
        User user = authenticationService.confirmUser(username, code);
        RegisterResponse registerResponse = new RegisterResponse();
        if (user == null) {
            registerResponse.setMessage("Invalid confirmation code");
            registerResponse.setUser(user);
            return ResponseEntity.badRequest().body(registerResponse);
        }
        registerResponse.setMessage("User registered successfully");
        registerResponse.setUser(user);
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/resend-email")
    public ResponseEntity<RegisterResponse> resendEmail(@Valid @RequestBody ConfirmationCodeDto confirmationCodeDto) {
        String username = confirmationCodeDto.getUsername();
        User user = authenticationService.resendEmail(username);
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setMessage("");
        registerResponse.setMessage("Confirmation email sent. Please check your email for further instructions.");
        registerResponse.setUser(user);
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/cancel")
    public ResponseEntity<RegisterResponse> cancel(@Valid @RequestBody ConfirmationCodeDto confirmationCodeDto) {
        String username = confirmationCodeDto.getUsername();
        Integer code = confirmationCodeDto.getCode();
        authenticationService.cancelRegistration(username);
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setMessage("User registration canceled successfully");
        registerResponse.setUser(null);
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<GeneralApiResponse> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid token");
        }
        String jwtToken = authorizationHeader.substring(7);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        String existingToken = signInTokenService.getToken(user);
        if (existingToken != null && jwtService.isTokenValid(existingToken, user) && jwtToken.equals(existingToken)) {
            signInTokenService.unregisterToken(user);
        }
        else{
            throw new InvalidTokenException("Invalid token");
        }
        SecurityContextHolder.clearContext();
        GeneralApiResponse generalApiResponse = new GeneralApiResponse();
        generalApiResponse.setMessage("User logged out successfully");
        return ResponseEntity.ok(generalApiResponse);
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginUserDto loginUserDto, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String device = null;
        if (userAgent.contains("Mobi")) {
            device = "Telefon";
        }
        else {
            device = "Desktop";
        }
        String identifier = loginUserDto.getIdentifier();
        String password = loginUserDto.getPassword();
        LoginResponse loginResponse = new LoginResponse();
        User possibleUser = authenticationService.findUserByIdentifier(identifier);
        String username = possibleUser.getUsername();
        if (possibleUser != null && (possibleUser.getCreatedWith().equals("GOOGLE") || possibleUser.getCreatedWith().equals("FACEBOOK"))) {
            loginResponse.setToken(null);
            loginResponse.setExpirationTime(0);
            loginResponse.setUsername(null);
            loginResponse.setMessage("User created with Google/Facebook. Please login with Google/Facebook.");
            return ResponseEntity.badRequest().body(loginResponse);
        }
        if (!authenticationService.isPasswordCorrect(username, password) || possibleUser == null) {
            loginResponse.setToken(null);
            loginResponse.setExpirationTime(0);
            loginResponse.setUsername(null);
            loginResponse.setMessage("Invalid username or password");
            return ResponseEntity.badRequest().body(loginResponse);
        }
        if (connectionDetailsService.isDifferentConnection(possibleUser, ipAddress, userAgent, device)) {
            PendingSignInUser pendingSignInUser = new PendingSignInUser();
            pendingSignInUser.setIdentifier(possibleUser.getUsername());
            pendingSignInUser.setPassword(loginUserDto.getPassword());
            pendingSignInUser.setRememberBrowser(loginUserDto.getRememberBrowser());
            pendingSignInUser.setUser(possibleUser);
            confirmationAuthService.addPendingSignInUser(pendingSignInUser);
            authenticationService.sendEmailForNewBrowser(possibleUser.getUsername());
            loginResponse.setToken(null);
            loginResponse.setExpirationTime(0);
            loginResponse.setUsername(possibleUser.getUsername());
            loginResponse.setMessage("New browser detected. Please check your email for further instructions.");
            return ResponseEntity.ok(loginResponse);
        }
        else{
            User user = authenticationService.loginUser(loginUserDto);

            String existingToken = signInTokenService.getToken(user);
            if (existingToken != null && jwtService.isTokenValid(existingToken, user)) {
                signInTokenService.unregisterToken(user);
                signInTokenService.notifyUserOfDesconnection(user);
            }
            String token = jwtService.generateToken(user);
            LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(jwtService.getExpirationTime()/1000);
            signInTokenService.registerToken(user, token, expiryDate);

            loginResponse.setToken(token);
            loginResponse.setExpirationTime(jwtService.getExpirationTime());
            loginResponse.setUsername(user.getUsername());
            loginResponse.setMessage("User logged in successfully");
            return ResponseEntity.ok(loginResponse);
        }
    }

    @PostMapping("/resend-new-browser-email")
    public ResponseEntity<GeneralApiResponse> resendNewBrowserEmail(@Valid @RequestBody ConfirmationCodeDto confirmationCodeDto) {
        String username = confirmationCodeDto.getUsername();
        authenticationService.sendEmailForNewBrowser(username);
        GeneralApiResponse generalApiResponse = new GeneralApiResponse();
        generalApiResponse.setMessage("Confirmation email sent. Please check your email for further instructions.");
        return ResponseEntity.ok(generalApiResponse);
    }

    @PostMapping("/confirm-new-browser")
    public ResponseEntity<LoginResponse> confirmNewBrowser(@Valid @RequestBody ConfirmationCodeDto confirmationCodeDto, HttpServletRequest request) {
        String username = confirmationCodeDto.getUsername();
        int code = confirmationCodeDto.getCode();

        PendingSignInUser pendingSignInUser = confirmationAuthService.getPendingSignInUser(username);
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setIdentifier(pendingSignInUser.getIdentifier());
        loginUserDto.setPassword(pendingSignInUser.getPassword());
        loginUserDto.setRememberBrowser(pendingSignInUser.getRememberBrowser());

        User user = authenticationService.confirmSignIn(username, code);
        LoginResponse loginResponse = new LoginResponse();
        if (user == null) {
            loginResponse.setToken(null);
            loginResponse.setExpirationTime(0);
            loginResponse.setUsername(null);
            loginResponse.setMessage("Invalid confirmation code");
            return ResponseEntity.badRequest().body(loginResponse);
        }
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String device = null;
        if (userAgent.contains("Mobi")) {
            device = "Telefon";
        }
        else {
            device = "Desktop";
        }

        user = authenticationService.loginUser(loginUserDto);
        String existingToken = signInTokenService.getToken(user);
        if (existingToken != null && jwtService.isTokenValid(existingToken, user)) {
            signInTokenService.unregisterToken(user);
            signInTokenService.notifyUserOfDesconnection(user);
        }
        String token = jwtService.generateToken(user);
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(jwtService.getExpirationTime()/1000);
        signInTokenService.registerToken(user, token, expiryDate);

        confirmationAuthService.removePendingSignInUser(pendingSignInUser);

        if(loginUserDto.getRememberBrowser().contains("true")) {
            connectionDetailsService.saveConnectionDetails(user, ipAddress, userAgent, device);
        }
        authenticationService.sendNewSignInAlertEmail(username, ipAddress, userAgent, device);

        loginResponse.setToken(token);
        loginResponse.setExpirationTime(jwtService.getExpirationTime());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setMessage("User logged in successfully");
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/cancel-signin")
    public ResponseEntity<LoginResponse> cancelSignIn(@Valid @RequestBody ConfirmationCodeDto confirmationCodeDto) {
        String username = confirmationCodeDto.getUsername();
        Integer code = confirmationCodeDto.getCode();
        authenticationService.cancelSignIn(username);

        PendingSignInUser pendingSignInUser = confirmationAuthService.getPendingSignInUser(username);
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setIdentifier(pendingSignInUser.getIdentifier());
        loginUserDto.setPassword(pendingSignInUser.getPassword());
        loginUserDto.setRememberBrowser(pendingSignInUser.getRememberBrowser());
        confirmationAuthService.removePendingSignInUser(pendingSignInUser);
        confirmationAuthService.removeConfirmationCode(username);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(null);
        loginResponse.setExpirationTime(0);
        loginResponse.setUsername(null);
        loginResponse.setMessage("Sign in canceled successfully");
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<GeneralApiResponse> forgotPassword(@Valid @RequestBody ForgotPaswordDto forgotPaswordDto) {
        String email = forgotPaswordDto.getEmail();
        authenticationService.forgotPassword(email);
        GeneralApiResponse generalApiResponse = new GeneralApiResponse();
        generalApiResponse.setMessage("Password reset email sent. Please check your email for further instructions.");
        return ResponseEntity.ok(generalApiResponse);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GeneralApiResponse> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        String token = resetPasswordDto.getToken();
        String newPassword = resetPasswordDto.getNewPassword();
        String confirmNewPassword = resetPasswordDto.getConfirmNewPassword();
        authenticationService.resetPassword(token, newPassword, confirmNewPassword);
        GeneralApiResponse generalApiResponse = new GeneralApiResponse();
        generalApiResponse.setMessage("Password reset successfully");
        return ResponseEntity.ok(generalApiResponse);
    }


    @DeleteMapping("/delete-connection-details")
    public ResponseEntity<GeneralApiResponse> deleteConnectionDetails(@Valid @RequestBody DeleteConnectionDetailDto deleteConnectionDetailDto,@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid token");
        }
        String jwtToken = authorizationHeader.substring(7);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        String existingToken = signInTokenService.getToken(user);
        if (existingToken != null && jwtService.isTokenValid(existingToken, user) && jwtToken.equals(existingToken)) {
            signInTokenService.unregisterToken(user);
        }
        else{
            throw new InvalidTokenException("Invalid token");
        }
        String idConnectDetails = deleteConnectionDetailDto.getId();
        connectionDetailsService.deleteConnectionDetails(idConnectDetails);
        SecurityContextHolder.clearContext();
        GeneralApiResponse generalApiResponse = new GeneralApiResponse();
        generalApiResponse.setMessage("Connection details deleted successfully");
        return ResponseEntity.ok(generalApiResponse);
    }

    @GetMapping("/all-connection-details-for-user")
    public ResponseEntity<List<ConnectionDetails>> allConnectionDetailsForUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid token");
        }
        String jwtToken = authorizationHeader.substring(7);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        String existingToken = signInTokenService.getToken(user);
        if (existingToken != null && jwtService.isTokenValid(existingToken, user) && jwtToken.equals(existingToken)) {
            List<ConnectionDetails> connectionDetails = connectionDetailsService.getAllConnectionsForUser(user);
            return ResponseEntity.ok(connectionDetails);
        }
        else{
            throw new InvalidTokenException("Invalid token");
        }
    }


}
