package service;

import domain.User;
import domain.VerificationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import repository.UserRepository;
import repository.VerificationCodeRepository;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationCodeRepository codeRepository;

    @Autowired
    private JavaMailSender mailSender;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ResponseEntity<String> register(String username, String rawPassword, String email) {
        User user = new User(username, rawPassword, email);
        user.setVerified(false);
        userRepository.save(user);
        sendVerificationCode(user);
        return ResponseEntity.ok("User registered successfully");
    }

    public void sendVerificationCode(User user) {
        VerificationCode code = new VerificationCode(user);
        codeRepository.save(code);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Your verification code");
        message.setText("Use this code to verify your account: " + code.getCode() +
                "\nExpires at: " + code.getExpiresAt());
        mailSender.send(message);
    }

    public ResponseEntity<String> verifyCode(String email, String codeStr) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("There is no user with that email");
        User user = userOpt.get();

        //to be safe , maybe somehow, he bypassed the code verification stuff ( ˘︹˘ )
        Optional<VerificationCode> codeOpt = codeRepository.findByCode(codeStr);
        if (codeOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("The verification code is invalid");
        VerificationCode code = codeOpt.get();

        if (!code.getUser().equals(user) || code.isExpired())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("The verification code is invalid or probably expired(check the date on the email)");
        return ResponseEntity.ok("User registered successfully via code received on the email");
    }

}
