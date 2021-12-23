package com.learning.accesscontrol.controller;

import com.learning.accesscontrol.entity.TokenType;
import com.learning.accesscontrol.entity.UserEntity;
import com.learning.accesscontrol.model.*;
import com.learning.accesscontrol.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @PostMapping(path = "/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        return new ResponseEntity<>(userService.register(request), HttpStatus.CREATED);
    }

    @GetMapping(path = "/confirmation/{token}")
    public ResponseEntity<MessageResponse> confirmToken(@PathVariable @Valid @NotBlank String token) {
        return new ResponseEntity<>(userService.confirmToken(token, TokenType.REGISTER), HttpStatus.OK);
    }

    @PostMapping(path = "/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody @Valid ForgotPwdRequest request) {
        return new ResponseEntity<>(userService.forgotPassword(request), HttpStatus.OK);
    }

    @PostMapping(path = "/change-password")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody @Valid ChangePwdRequest request) {
        return new ResponseEntity<>(userService.changePassword(request), HttpStatus.OK);
    }

    @GetMapping(path = "/")
    public ResponseEntity<List<UserEntity>> getAllUser(@RequestHeader HttpHeaders headers) {
        String accessToken = headers.get("authorization").toString();
        return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK);
    }
}
