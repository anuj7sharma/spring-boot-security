package com.learning.springSecurity.service;

import com.learning.springSecurity.email.Email;
import com.learning.springSecurity.email.EmailSender;
import com.learning.springSecurity.entity.TokenEntity;
import com.learning.springSecurity.entity.TokenType;
import com.learning.springSecurity.entity.UserEntity;
import com.learning.springSecurity.entity.Role;
import com.learning.springSecurity.exception.BadRequestException;
import com.learning.springSecurity.exception.NotFoundException;
import com.learning.springSecurity.model.RegisterRequest;
import com.learning.springSecurity.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService, IUserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenService tokenService;
    private final EmailSender emailSender;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findByEmail(username);
        if (userEntity.isEmpty()) {
            throw new NotFoundException(String.format("User not found with %s", username));
        }
        if (Boolean.TRUE.equals(userEntity.get().getLocked())) {
            throw new BadRequestException("User is locked");
        }
        if (Boolean.FALSE.equals(userEntity.get().getEnabled())) {
            throw new BadRequestException("User is not enabled");
        }
        return userEntity.get();
    }

    @Override
    @Transactional
    public String register(RegisterRequest request) {
        Optional<UserEntity> user = userRepository.findByEmail(request.getEmail());
        if (user.isPresent()) {
            // TODO: Handle the case if user is registered but not verified.
            throw new BadRequestException("User already exists with this email");
        }
        String encodedPassword = bCryptPasswordEncoder
                .encode(request.getPassword());

        UserEntity userEntity = new UserEntity(request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                encodedPassword,
                Role.USER);
        userRepository.save(userEntity);
        // Generate Confirmation Token
        TokenEntity token = new TokenEntity(userEntity, UUID.randomUUID().toString(),
                LocalDateTime.now().plusMinutes(15),
                TokenType.REGISTER);
        tokenService.saveConfirmationToken(token);
        // Send Confirmation Email to the registered EmailId
        String confirmationLink = "http://localhost:8090/api/v1/user/confirmation/" + token.getToken();
//        emailSender.send(buildRegisterConfirmationEmail(userEntity, confirmationLink));

        return "User registered. Confirmation link sent to the registered Email. " + confirmationLink;
    }

    @Override
    public String confirmToken(String token, TokenType tokenType) {
        Optional<TokenEntity> tokenEntity = tokenService.getConfirmationToken(token);
        if (tokenEntity.isEmpty()) {
            throw new BadRequestException("Verification token is not valid");
        }
        if (tokenEntity.get().getConfirmedAt() != null) {
            throw new BadRequestException("Token already verified");
        }
        if (LocalDateTime.now().isBefore(tokenEntity.get().getExpiresAt())) {
            tokenService.updateTokenConfirmationTime(token);
            enableUser(tokenEntity.get().getUser().getUsername());
        }
        return String.format("%s is confirmed", tokenEntity.get().getUser().getEmail());
    }

    @Override
    public void enableUser(String username) {
        userRepository.enableUser(username);
    }

    private Email buildRegisterConfirmationEmail(UserEntity userEntity, String confirmationLink) {
        Email email = new Email();
        email.setFrom("confirmation@anuj-acadamy.com");
        email.setTo(userEntity.getEmail());
        email.setSubject("Security Demo App Register Confirmation");
        email.setTemplateName("register_confirmation.html");
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("firstName", userEntity.getFirstName());
        valueMap.put("lastName", userEntity.getLastName());
        valueMap.put("link", confirmationLink);
        email.setModel(valueMap);
        return email;
    }
}
