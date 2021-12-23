package com.learning.accesscontrol.service;

import com.learning.accesscontrol.email.Email;
import com.learning.accesscontrol.email.EmailSender;
import com.learning.accesscontrol.entity.TokenEntity;
import com.learning.accesscontrol.entity.TokenType;
import com.learning.accesscontrol.entity.UserEntity;
import com.learning.accesscontrol.entity.Role;
import com.learning.accesscontrol.exception.BadRequestException;
import com.learning.accesscontrol.exception.CustomException;
import com.learning.accesscontrol.exception.NotFoundException;
import com.learning.accesscontrol.model.*;
import com.learning.accesscontrol.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService implements UserDetailsService, IUserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ITokenService tokenService;
    @Autowired
    private EmailSender emailSender;

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
            emailSender.send(buildEmailObj(userEntity, confirmationLink, "register_verification", "Registration successful"));
        } catch (MessagingException e) {
            logger.error("Exception occurred while sending signup registration", e);
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
    @Transactional
    public MessageResponse forgotPassword(@Valid ForgotPwdRequest request) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(request.getEmail());
        if (userEntity.isEmpty()) {
            throw new NotFoundException("Email does not exist");
        } else if (Boolean.FALSE.equals(userEntity.get().getEnabled())) {
            throw new BadRequestException("This email is disabled. Please enable it through sign-up process");
        }
        String uniqueToken;

        // TODO: Logic to be added to maintain the DB persistance if multiple entries from same user occurred
        /*
        Optional<TokenEntity> token = tokenService.getTokenByTypeAndUserId(TokenType.FORGOT_PASSWORD, userEntity.get());
        if (token.isPresent() && token.get().getConfirmedAt() == null) {
            // User already requested for forgot-password, but didn't confirm it.
            logger.info("This user already requested for forgot-password>> " + userEntity.get().getEmail());
            // Update already stored token expiredAt
            tokenService.updateTokenExpiredTime(token.get().getToken(), LocalDateTime.now().plusMinutes(15));
            uniqueToken = token.get().getToken();
        } else {

        }
        */
        TokenEntity tokenEntity = new TokenEntity();
        uniqueToken = UUID.randomUUID().toString();
        tokenEntity.setUser(userEntity.get());
        tokenEntity.setTokenType(TokenType.FORGOT_PASSWORD);
        tokenEntity.setToken(uniqueToken);
        tokenEntity.setCreatedOn(LocalDateTime.now());
        tokenEntity.setUpdatedOn(LocalDateTime.now());
        tokenEntity.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        tokenService.saveConfirmationToken(tokenEntity);
        // Send ResetPassword Email to the registered user
        try {
            emailSender.send(buildEmailObj(userEntity.get(), uniqueToken, "forgot_password", "Forgot Your Password? Change It."));
        } catch (MessagingException e) {
            logger.error("Exception occurred while sending forgot-password email", e);
        }
        return new MessageResponse("Email has been sent to the registered Email. Please follow steps to change your password");
    }

    @Override
    public MessageResponse changePassword(ChangePwdRequest request) {
        Optional<TokenEntity> tokenEntity = tokenService.getConfirmationToken(request.getToken());
        if (tokenEntity.isEmpty()) {
            throw new NotFoundException("Invalid token provided");
        } else if (LocalDateTime.now().isAfter(tokenEntity.get().getExpiresAt())) {
            throw new BadRequestException("Token expired, Please re-request for forgot password");
        } else if (tokenEntity.get().getConfirmedAt() != null) {
            throw new BadRequestException("Token already used.");
        }
        String encodedPassword = bCryptPasswordEncoder
                .encode(request.getPassword());
        userRepository.updateUserPassword(encodedPassword, tokenEntity.get().getUser().getId());
        //Update token update time to make sure it will never be re-used.
        tokenService.updateTokenConfirmationTime(request.getToken());
        return new MessageResponse("Congratulations!! your password has been changed successfully.");
    }

    @Override
    public void enableUser(String username) {
        userRepository.enableUser(username);
    }

    @Override
    public List<UserEntity> getAllUser() {
        return userRepository.findAll();
    }

    private Email buildEmailObj(UserEntity user, String confirmationLink, String template, String subject) {
        Email email = new Email();
        email.setSubject(subject);
        email.setTemplateName(template);
        email.setSendTo(user.getEmail());
        Map<String, Object> valueModel = new HashMap<>();
        valueModel.put("firstName", user.getFirstName());
        valueModel.put("lastName", user.getLastName());
        valueModel.put("confirmationLink", confirmationLink);
        email.setValueModel(valueModel);
        return email;
    }

}
