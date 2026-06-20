package com.droppa.DroppaUserService.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.droppa.DroppaUserService.dto.UserResponseDTO;
import com.droppa.DroppaUserService.entity.Person;
import com.droppa.DroppaUserService.entity.Token;
import com.droppa.DroppaUserService.entity.UserAccount;
import com.droppa.DroppaUserService.exception.IncorrectPasswordException;
import com.droppa.DroppaUserService.exception.UserNotFoundException;
import com.droppa.DroppaUserService.repository.PersonRepository;
import com.droppa.DroppaUserService.repository.TokenRepository;
import com.droppa.DroppaUserService.repository.UserAccountRepository;
import com.droppa.DroppaUserService.testdata.RequestMother;
import com.droppa.DroppaUserService.testdata.UserResponseMother;


@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
	
	@Mock
	private PersonRepository personRepository;
	
	@Mock
	private UserAccountRepository userAccountRepository;
	
	@Mock
	private PartyService partyService;
	
	@Mock
	private Person person;
	
	@Mock
	private TokenRepository tokenRepository;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Mock
	private JwtService jwtService;
	
	@Mock
	private UserService userService;

	@InjectMocks
	private AuthenticationService authenticationService;
	
	@Test
	@DisplayName("Should create user account successfully")
	void shouldCreateUserAccountSuccessfully() {

		var request = RequestMother.validPerson();
	    

	    when(userAccountRepository.findByEmail(request.getEmail()))
	            .thenReturn(Optional.empty());

	    when(personRepository.findByEmail(request.getEmail()))
	            .thenReturn(Optional.empty());

	    when(partyService.generateOTP(request.getEmail()))
	            .thenReturn("12345");

	    when(passwordEncoder.encode(anyString()))
	            .thenReturn("encoded-pass");

	    when(jwtService.generateToken(any()))
	            .thenReturn("jwt-token");

	    when(userAccountRepository.save(any()))
	            .thenAnswer(invocation -> invocation.getArgument(0));

	    when(userService.buildUserResponse(any(), anyString()))
	            .thenReturn( UserResponseMother.minimalUser()                    
	            );

	    UserResponseDTO response =
	            authenticationService.createUserAccount(request);

	    assertEquals("ernest@gmail.com", response.getEmail());
	    assertEquals("jwt-token", response.getToken());

	    verify(userService).buildUserResponse(any(), anyString());
	    verify(userAccountRepository).save(any(UserAccount.class));
	}
	
	
	@Test
	@DisplayName("Should confirm email successfully")
	void shouldConfirmEmailSuccessfully() {

	    var request = RequestMother.confirmEmail();

	    UserAccount user = mock(UserAccount.class);
	    Token token = Token.builder()
	            .token("jwt-token")
	            .build();

	    when(userService.getUserByEmail(request.getEmail()))
	            .thenReturn(user);

	    when(tokenRepository.findByUserId(anyInt()))
	            .thenReturn(Optional.of(token));

	    when(userService.buildUserResponse(user, "jwt-token"))
	            .thenReturn( UserResponseMother.defaultUser());

	    UserResponseDTO response =
	            authenticationService.confirmEmail(request);

	    assertEquals("ernest@gmail.com", response.getEmail());
	}
	
	@Test
	@DisplayName("Should request password reset")
	void shouldRequestPasswordReset() {

	    UserAccount user = mock(UserAccount.class);
	    
	    Person person = mock(Person.class);

	    when(userService.getUserByEmail("ernest@gmail.com"))
	            .thenReturn(user);

	    when(user.getPerson()).thenReturn(person);
	    when(person.getCellphone())
	            .thenReturn("0721010222");

	    when(partyService.generateOTP("0721010222"))
	            .thenReturn("99999");

	    String otp = authenticationService.requestPasswordReset("ernest@gmail.com");

	    assertEquals("99999", otp);

	    verify(user).requestPasswordReset("99999");
	}
	
	
	@Test
	@DisplayName("Should reset password successfully")
	void shouldResetPasswordSuccessfully() {
	    
	    var request = RequestMother.resetPassword();

	    UserAccount user = mock(UserAccount.class);

	    when(userService.getUserByEmail(request.getUsername()))
	            .thenReturn(user);

	    when(passwordEncoder.encode(request.getPassword()))
	            .thenReturn("encoded");

	    when(jwtService.generateToken(any()))
	            .thenReturn("jwt-token");

	    when(userService.buildUserResponse(any(), anyString()))
	            .thenReturn( UserResponseMother.minimalUser()
	                   
	            );

	    UserResponseDTO response =
	            authenticationService.resetPassword(request);

	    assertEquals("ernest@gmail.com", response.getEmail());
	}
	
	
	@Test
	@DisplayName("Should auhenticate user successfully")
	void shouldAuthenticateUserSuccessfully() {
	    
	    var request = RequestMother.validCredentials();

	    UserAccount user = mock(UserAccount.class);

	    when(userService.getUserByEmail(request.getUsername()))
	            .thenReturn(user);

	    when(user.getEmail())
	            .thenReturn(request.getUsername());

	    when(user.getEncodedPassword())
	            .thenReturn("encoded-password");

	    when(passwordEncoder.matches(request.getPassword(), "encoded-password"))
	            .thenReturn(true);

	    when(jwtService.generateToken(any()))
	            .thenReturn("jwt-token");
	    
	    when(tokenRepository.findAllByUserIdAndExpiredFalseAndRevokedFalse(anyInt()))
        .thenReturn(List.of(new Token()));
	    
	    when(userService.buildUserResponse(any(), anyString()))
        .thenReturn( UserResponseMother.minimalUser()
               
        );
	    

	    UserResponseDTO response =
	            authenticationService.authenticate(request);

	    assertEquals("ernest@gmail.com", response.getEmail());
	    assertEquals("jwt-token", response.getToken());

	    verify(tokenRepository).saveAll(any());
	    verify(tokenRepository).save(any());
	}

	@Test
	@DisplayName("Should throw user not found when login account does not exist")
	void shouldThrowUserNotFoundWhenLoginAccountDoesNotExist() {

	    var request = RequestMother.validCredentials();

	    when(userService.getUserByEmail(request.getUsername()))
	            .thenThrow(new UserNotFoundException(request.getUsername()));

	    UserNotFoundException exception = assertThrows(
	            UserNotFoundException.class,
	            () -> authenticationService.authenticate(request)
	    );

	    assertEquals(
	            "Account not found for email: " + request.getUsername(),
	            exception.getMessage()
	    );
	}

	@Test
	@DisplayName("Should throw incorrect password when login password is invalid")
	void shouldThrowIncorrectPasswordWhenLoginPasswordIsInvalid() {

	    var request = RequestMother.validCredentials();

	    UserAccount user = mock(UserAccount.class);

	    when(userService.getUserByEmail(request.getUsername()))
	            .thenReturn(user);
	    when(user.getEncodedPassword())
	            .thenReturn("encoded-password");
	    when(passwordEncoder.matches(request.getPassword(), "encoded-password"))
	            .thenReturn(false);

	    IncorrectPasswordException exception = assertThrows(
	            IncorrectPasswordException.class,
	            () -> authenticationService.authenticate(request)
	    );

	    assertEquals("Incorrect password", exception.getMessage());
	}

}
