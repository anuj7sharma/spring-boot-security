package com.learning.springSecurity.service;

import com.learning.springSecurity.email.Email;
import com.learning.springSecurity.email.EmailSender;
import com.learning.springSecurity.entity.TokenEntity;
import com.learning.springSecurity.entity.TokenType;
import com.learning.springSecurity.entity.UserEntity;
import com.learning.springSecurity.entity.Role;
import com.learning.springSecurity.exception.BadRequestException;
import com.learning.springSecurity.exception.CustomException;
import com.learning.springSecurity.exception.NotFoundException;
import com.learning.springSecurity.model.MessageResponse;
import com.learning.springSecurity.model.RegisterRequest;
import com.learning.springSecurity.model.RegisterResponse;
import com.learning.springSecurity.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

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
    public RegisterResponse register(RegisterRequest request) {
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
        try {
            emailSender.send(getRegisterEmailObj(userEntity, confirmationLink));
        } catch (MessagingException e) {
            throw new CustomException(HttpStatus.FORBIDDEN, 503, "Forbidden", "Error while sending Email", LocalDateTime.now());
        }

        String message = "User has been registered successfully. Confirmation link sent to the registered Email. " + confirmationLink;
        return new RegisterResponse(userEntity.getFirstName(), userEntity.getLastName(), userEntity.getEmail(), message);
    }

    @Override
    public MessageResponse confirmToken(String token, TokenType tokenType) {
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
        return new MessageResponse(String.format("%s is confirmed. Please login", tokenEntity.get().getUser().getEmail()));
    }

    @Override
    public void enableUser(String username) {
        userRepository.enableUser(username);
    }

    @Override
    public List<UserEntity> getAllUser() {
        return userRepository.findAll();
    }

    private Email getRegisterEmailObj(UserEntity user, String confirmationLink) {
        Email email = new Email();
        email.setSubject("Registration successful");
        email.setTemplateName("register_verification");
        email.setSendTo(user.getEmail());
        Map<String, Object> valueModel = new HashMap<>();
        valueModel.put("firstName", user.getFirstName());
        valueModel.put("lastName", user.getLastName());
        valueModel.put("confirmationLink", confirmationLink);
        email.setValueModel(valueModel);
        return email;
    }
}
