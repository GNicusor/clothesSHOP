package server;

import com.stripe.model.oauth.TokenResponse;
import domain.User;
import domain.VerificationCode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import repository.UserRepository;
import repository.VerificationCodeRepository;
import service.AppConfig;
import service.AuthenticationService;
import shared.LoginRequest;
import shared.RegisterRequest;
import shared.VerifyRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;



@RestController
@CrossOrigin
@RequestMapping("/login")
public class ControllerAuthentication {

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email is already in use");
        }
        authService.register(req.getUsername(), req.getPassword(), req.getEmail());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@Valid @RequestBody VerifyRequest req) {
        Optional<User> userOpt = userRepository.findByEmail(req.getEmail());
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No user found with that email");
        }
        User user = userOpt.get();

        Optional<VerificationCode> codeOpt = verificationCodeRepository.findByCode(req.getCode());
        if (!codeOpt.isPresent() || !codeOpt.get().getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The verification code is invalid");
        }
        VerificationCode code = codeOpt.get();

        if (code.isExpired()) {
            verificationCodeRepository.delete(code);
            authService.sendVerificationCode(user);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The code has expired. A new code has been sent to your email.");
        }

        verificationCodeRepository.delete(code);
        user.setVerified(true);
        userRepository.save(user);
        return ResponseEntity.ok("User successfully verified via email code");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        Optional<User> userOpt = userRepository.findByUsername(req.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "There is no user with that username"));
        }
        User user = userOpt.get();
        if (!user.isVerified()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "The account is not verified"));
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Incorrect password"));
        }

        // Build a JSON response that includes userId and username
        Map<String, Object> resp = new HashMap<>();
        resp.put("userId", user.getId());
        resp.put("username", user.getUsername());
        resp.put("message", "User logged in successfully");
        return ResponseEntity.ok(resp);
    }

}
