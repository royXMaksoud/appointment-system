package com.ftp.authservice.application.service;

import com.ftp.authservice.application.command.RegisterUserCommand;
import com.ftp.authservice.domain.model.User;
import com.ftp.authservice.domain.model.UserType;
import com.ftp.authservice.domain.ports.out.LoadUserPort;
import com.ftp.authservice.domain.ports.out.SaveUserPort;
import com.ftp.authservice.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * ✅ Unit Test for RegisterServiceImpl
 *
 * Goal:
 * This test verifies that when a valid RegisterUserCommand is passed to the service,
 * a new User object is created and passed to SaveUserPort, and the correct User is returned.
 */
public class RegisterServiceImplTest {

    private SaveUserPort saveUserPort;              // Mocked dependency
    private LoadUserPort loadUserPort;
    private RegisterServiceImpl service;            // Class under test

    @BeforeEach
    void setUp() {
        // ✅ Create mock for SaveUserPort
        saveUserPort = Mockito.mock(SaveUserPort.class);
        loadUserPort = Mockito.mock(LoadUserPort.class);


        // ✅ Inject mock into service implementation
        service = new RegisterServiceImpl(saveUserPort, loadUserPort);
    }

    @Test
    public void testRegisterNewUser_Success() {
        // ✅ Step 1: Prepare a valid registration command
        RegisterUserCommand command = new RegisterUserCommand(
                "test",            // full name
                "test@y.com",      // email
                "1230",            // password
                "1230",            // confirm password
                "USER"   ,          // type
                "en"
        );

        // ✅ Step 2: Create the expected User object to be returned by saveUserPort
        User expectedUser = new User(
                UUID.randomUUID(),   // ID (random for test)
                "test",              // full name
                "test@y.com",        // email
                "1230",              // password
                UserType.USER,       // type
                true,                // enabled
                false,               // deleted
                Instant.now(),       // created at
                null    ,             // last login
                "en"
        );

        // ✅ Step 3: Configure mock to return expectedUser when saveUser() is called
        Mockito.when(saveUserPort.saveUser(Mockito.any(User.class)))
                .thenReturn(expectedUser);

        // ✅ Step 4: Call the method under test
        User actualUser = service.register(command);

        // ✅ Step 5: Verify the result
        Assertions.assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        Assertions.assertEquals(expectedUser.getFullName(), actualUser.getFullName());
        Assertions.assertEquals(expectedUser.getType(), actualUser.getType());

        // ✅ Step 6: Verify that saveUser was called exactly once
        Mockito.verify(saveUserPort, Mockito.times(1)).saveUser(Mockito.any(User.class));
    }

    /**
     * ✅ Test: Registering with a duplicate email should throw exception.
     */
    @Test
    public void testRegisterUserWithDuplicateEmail_ShouldThrowException() {
        // 1. Prepare a registration command with a duplicate email
        RegisterUserCommand command = new RegisterUserCommand(
                "test", "duplicate@email.com", "pass", "pass",
                "USER","en"
        );

        // 2. Simulate that a user already exists with this email
        User existingUser = new User(
                UUID.randomUUID(),
                "existing",
                "duplicate@email.com",
                "pass",
                UserType.USER,
                true,
                false,
                Instant.now(),
                null,
                "en"
        );
        Mockito.when(loadUserPort.loadUserByEmail("duplicate@email.com"))
                .thenReturn(Optional.of(existingUser));

        // 3. Assert that an exception is thrown during registration
        Assertions.assertThrows(UserAlreadyExistsException.class, () -> {
            service.register(command);
        });
        // 4. Verify that saveUser() was NEVER called due to early validation failure
        Mockito.verify(saveUserPort, Mockito.never()).saveUser(Mockito.any(User.class));
    }

}
