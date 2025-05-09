package me.artemiyulyanov.uptodate.controllers.api.auth;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.artemiyulyanov.uptodate.controllers.AuthenticatedController;
import me.artemiyulyanov.uptodate.controllers.api.auth.requests.LoginRequest;
import me.artemiyulyanov.uptodate.controllers.api.auth.requests.RegisterRequest;
import me.artemiyulyanov.uptodate.controllers.api.auth.responses.TokenResponse;
import me.artemiyulyanov.uptodate.jwt.JWTUtil;
import me.artemiyulyanov.uptodate.mail.MailConfirmationMessage;
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
@Tag(name = "Auth", description = "Endpoints to proceed authentication requests")
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

    @Operation(summary = "Gets user logged in")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "The user is invalid!"),
            @ApiResponse(responseCode = "200", description = "The user has been signed in successfully!")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (!userService.isUserVaild(username, password)) {
            return requestService.executeApiResponse(HttpStatus.UNAUTHORIZED, "User is invalid!");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        Optional<User> wrappedUser = userService.getUserByUsername(username);
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

    @Operation(summary = "Signs up a new user", description = "The initial step to be confirmed with a confirmation link")
    @ApiResponses({
            @ApiResponse(responseCode = "409", description = "User already exists!"),
            @ApiResponse(responseCode = "200", description = "The confirmation link has been sent successfully!")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();

        if (userService.userExists(username, email)) {
            return requestService.executeApiResponse(HttpStatus.CONFLICT, "User already exists!");
        }

        registerRequest.setPassword(password);
        MailConfirmationMessage mailConfirmationMessage = mailSenderFactory.createSender(MailConfirmationMessage.MailScope.REGISTRATION)
                .send(email, List.of(
                        MailConfirmationMessage.Credential
                                .builder()
                                .key("registerRequest")
                                .value(registerRequest)
                                .build()
                ));

        return requestService.executeApiResponse(HttpStatus.OK, "The confirmation link has been sent to your email!");
    }

    @Operation(summary = "Confirms an expected user to be signed up")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "The confirmation link is invalid or not found!"),
            @ApiResponse(responseCode = "417", description = "The registration is not managed!"),
            @ApiResponse(responseCode = "202", description = "The user has been confirmed and signed up successfully!")
    })
    @PostMapping("/register/confirm/{id}")
    public ResponseEntity<?> registerConfirm(
            @Parameter(name = "The ID of confirmation link")
            @PathVariable
            String id
    ) {
        if (!mailService.hasMailConfirmationMessage(id)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The confirmation message is not found!");
        }

        MailConfirmationMessage mailConfirmationMessage = mailService.getMailConfirmationMessage(id);
        if (!mailConfirmationMessage.getScope().equals(MailConfirmationMessage.MailScope.REGISTRATION)) {
            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "The confirmation message id is invalid!");
        }

        RegisterRequest registerRequest = mailConfirmationMessage.getCredential("registerRequest").getValue(RegisterRequest.class);
        mailService.performConfirmationFor(id);

        User user = userService.createUser(registerRequest.getEmail(), registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getFirstName(), registerRequest.getLastName());
        if (user == null) {
            return requestService.executeApiResponse(HttpStatus.EXPECTATION_FAILED, "The registration has been failed!");
        }

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
}