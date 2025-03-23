package me.artemiyulyanov.uptodate.controllers.api.auth;

import me.artemiyulyanov.uptodate.controllers.AuthenticatedController;
import me.artemiyulyanov.uptodate.controllers.api.auth.requests.LoginRequest;
import me.artemiyulyanov.uptodate.controllers.api.auth.requests.RegisterRequest;
import me.artemiyulyanov.uptodate.controllers.api.auth.requests.VerifyCodeRequest;
import me.artemiyulyanov.uptodate.controllers.api.auth.responses.TokenResponse;
import me.artemiyulyanov.uptodate.jwt.JWTUtil;
import me.artemiyulyanov.uptodate.mail.MailConfirmationCode;
import me.artemiyulyanov.uptodate.mail.MailService;
import me.artemiyulyanov.uptodate.mail.senders.MailSenderFactory;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.web.RequestService;
import me.artemiyulyanov.uptodate.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends AuthenticatedController {
    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private RequestService requestService;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MailSenderFactory mailSenderFactory;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (!userService.isUserVaild(username, password)) {
            return requestService.executeApiResponse(HttpStatus.UNAUTHORIZED, "User is invalid!");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        Optional<User> wrappedUser = userService.findByUsername(username);
        UserDetails userDetails = userService.loadUserByUsername(username);

        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return requestService.executeCustomResponse(
                TokenResponse.builder()
                    .status(HttpStatus.ACCEPTED.value())
                    .access_token(accessToken)
                    .refresh_token(refreshToken)
                    .build()
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();

        if (userService.userExists(username, email)) {
            return requestService.executeApiResponse(HttpStatus.CONFLICT, "User already exists!");
        }

        registerRequest.setPassword(password);
        MailConfirmationCode mailConfirmationCode = mailSenderFactory.createSender(MailConfirmationCode.MailScope.REGISTRATION)
                .send(email, List.of(
                        MailConfirmationCode.Credential
                                .builder()
                                .key("registerRequest")
                                .value(registerRequest)
                                .build()
                ));

        return requestService.executeApiResponse(HttpStatus.OK, "The request has been proceeded successfully!");
    }

    @PostMapping("/register/verify-code")
    public ResponseEntity<?> registerVerifyCode(@RequestBody VerifyCodeRequest verifyCodeRequest) {
        String email = verifyCodeRequest.getEmail();
        String code = verifyCodeRequest.getCode();

        if (!mailService.validateCode(email, code, MailConfirmationCode.MailScope.REGISTRATION)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The code is invalid!");
        }

        MailConfirmationCode mailConfirmationCode = mailService.getConfirmationCode(email);
        RegisterRequest registerRequest = mailConfirmationCode.getCredential("registerRequest").getValue(RegisterRequest.class);

        mailService.enterCode(email, code, MailConfirmationCode.MailScope.REGISTRATION);
        User user = userService.create(registerRequest.getEmail(), registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getFirstName(), registerRequest.getLastName());

        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return requestService.executeCustomResponse(
                TokenResponse.builder()
                        .status(HttpStatus.ACCEPTED.value())
                        .access_token(accessToken)
                        .refresh_token(refreshToken)
                        .build()
        );
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestParam String refreshToken) {
        if (!jwtUtil.isTokenValid(refreshToken) || !jwtUtil.extractScope(refreshToken).equalsIgnoreCase("REFRESH")) {
            return requestService.executeApiResponse(HttpStatus.CONFLICT, "Refresh token is invalid!");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userService.loadUserByUsername(username);

        String accessToken = jwtUtil.generateAccessToken(userDetails);
        return requestService.executeCustomResponse(
                TokenResponse.builder()
                        .status(HttpStatus.OK.value())
                        .access_token(accessToken)
                        .build()
        );
    }
}