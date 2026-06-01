package com.droppa.DroppaUserService.service;

import com.droppa.DroppaUserService.dto.UserResponseDTO;
import com.droppa.DroppaUserService.entity.Person;
import com.droppa.DroppaUserService.entity.UserAccount;
import com.droppa.DroppaUserService.exception.ClientException;
import com.droppa.DroppaUserService.repository.UserAccountRepository;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserService userService;


    @Test
    @DisplayName("Should return user when email exists")
    void shouldReturnUserByEmail() {

        UserAccount user = mock(UserAccount.class);

        when(userAccountRepository.findByEmail("ernest@gmail.com"))
                .thenReturn(Optional.of(user));

        UserAccount result = userService.getUserByEmail("ernest@gmail.com");

        assertNotNull(result);
        verify(userAccountRepository).findByEmail("ernest@gmail.com");
    }


    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {

        when(userAccountRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        ClientException exception =
                assertThrows(ClientException.class,
                        () -> userService.getUserByEmail("missing@gmail.com"));

        assertEquals("Account not found", exception.getMessage());
        verify(userAccountRepository).findByEmail("missing@gmail.com");
    }


    @Test
    @DisplayName("Should build user response DTO correctly")
    void shouldBuildUserResponse() {

        Person person = Person.create(
                "Ernest",
                "Mohlala",
                "0723568069",
                "ernest@gmail.com"
        );

        UserAccount user = mock(UserAccount.class);

        when(user.getPerson()).thenReturn(person);
        when(user.getEmail()).thenReturn("ernest@gmail.com");

        String token = "jwt-token";

        UserResponseDTO response =
                userService.buildUserResponse(user, token);

        assertEquals("Ernest", response.getUserName());
        assertEquals("Mohlala", response.getSurname());
        assertEquals("0723568069", response.getCellphone());
        assertEquals("jwt-token", response.getToken());
        assertEquals("ernest@gmail.com", response.getEmail());
        assertEquals(BigDecimal.ZERO, response.getWalletBalance());
    }
}