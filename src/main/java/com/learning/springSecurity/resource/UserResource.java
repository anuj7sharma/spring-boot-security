package com.learning.springSecurity.resource;

import com.learning.springSecurity.entity.TokenType;
import com.learning.springSecurity.model.RegisterRequest;
import com.learning.springSecurity.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping(path = "/api/v1/user")
@AllArgsConstructor
public class UserResource {
    private final IUserService userService;

    @PostMapping(path = "/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        return new ResponseEntity<>(userService.register(request), HttpStatus.CREATED);
    }

    @GetMapping(path = "/confirmation/{token}")
    public ResponseEntity<String> confirmToken(@PathVariable @Valid @NotBlank String token) {
        return new ResponseEntity<>(userService.confirmToken(token, TokenType.REGISTER), HttpStatus.OK);
    }
}
