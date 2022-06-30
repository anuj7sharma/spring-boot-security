package service;


import com.learning.accesscontrol.email.EmailSender;
import com.learning.accesscontrol.entity.Role;
import com.learning.accesscontrol.entity.UserEntity;
import com.learning.accesscontrol.exception.BadRequestException;
import com.learning.accesscontrol.exception.NotFoundException;
import com.learning.accesscontrol.model.RegisterRequest;
import com.learning.accesscontrol.model.RegisterResponse;
import com.learning.accesscontrol.repository.UserRepository;
import com.learning.accesscontrol.service.ITokenService;
import com.learning.accesscontrol.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private ITokenService tokenService;
    @Mock
    private EmailSender emailSender;
    @InjectMocks
    private UserService userService;

    @Test
    void register_GivenValidDetails_ReturnsCorrectData() {
        // Arrange
        String firstName = "Anuj";
        String lastName = "Sharma";
        String email = "anuj@gmail.com";
        String password = "12345678";
        RegisterRequest request = new RegisterRequest(firstName, lastName, email, password);
        UserEntity userEntity = new UserEntity(request.getFirstName(), request.getLastName(), request.getEmail(),
                request.getPassword(), Role.USER);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        // Act
        RegisterResponse response = userService.register(request);

        // Assert
        assertAll("User should be registered",
                () -> assertNotNull(response),
                () -> assertEquals(email, response.getEmail()),
                () -> assertEquals(firstName, response.getFirstName()),
                () -> assertEquals(lastName, response.getLastName()));
    }

    @Test
    void register_GivenUserAlreadyExists_ThrowsBadRequestException() {
        // Arrange
        String firstName = "Anuj";
        String lastName = "Sharma";
        String email = "anuj@gmail.com";
        String password = "12345678";
        RegisterRequest request = new RegisterRequest(firstName, lastName, email, password);
        UserEntity userEntity = new UserEntity(request.getFirstName(), request.getLastName(), request.getEmail(),
                request.getPassword(), Role.USER);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        // Act, Assert
        assertThrows(BadRequestException.class, () -> userService.register(request));
    }

    @Test
    void loadUserByUserName_GivenValidDetails_ReturnsCorrectData() {
        // Arrange
        String firstName = "Anuj";
        String lastName = "Sharma";
        String email = "anuj@gmail.com";
        String password = "12345678";
        UserEntity userEntity = new UserEntity(firstName, lastName, email,
                password, Role.USER);
        userEntity.setEnabled(true);
        userEntity.setLocked(false);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        // Act
        UserDetails userDetails = userService.loadUserByUsername(email);

        // Assert
        assertAll("Valid information should be returned",
                () -> assertNotNull(userDetails),
                () -> assertEquals(email, userDetails.getUsername()));
    }

    @Test
    void loadUserByUserName_GivenUserNotPresent_ThrowsNotFoundException() {
        // Arrange
        String email = "anuj@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act, Assert
        assertThrows(NotFoundException.class, () -> userService.loadUserByUsername(email));
    }

    @Test
    void loadUserByUserName_GivenUserNotEnabled_ThrowsBadRequestException() {
        // Arrange
        String firstName = "Anuj";
        String lastName = "Sharma";
        String email = "anuj@gmail.com";
        String password = "12345678";
        UserEntity userEntity = new UserEntity(firstName, lastName, email,
                password, Role.USER);
        userEntity.setEnabled(false);
        userEntity.setLocked(false);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        // Act, Assert
        assertThrows(BadRequestException.class, () -> userService.loadUserByUsername(email));
    }
}
