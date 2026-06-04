package com.droppa.DroppaUserService.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.droppa.DroppaUserService.enums.AccountStatus;
import com.droppa.DroppaUserService.enums.Role;
import com.droppa.DroppaUserService.exception.ClientException;
import com.droppa.DroppaUserService.exception.OtpExpiredException;

class UserAccountTest {

    private Person person;
    private UserAccount account;


    @BeforeEach
    void setUp() {
        person = Person.create(
                "Ernest",
                "Mohlala",
                "0723568069",
                "ernest@gmail.com"
        );

        account = createAccount("12345");
    }

    private UserAccount createAccount(String otp) {
        return createAccount(otp, LocalDateTime.now().plusMinutes(10));
    }

    private UserAccount createAccount(String otp, LocalDateTime expiry) {
        return UserAccount.createPendingAccount(
                "ernest@gmail.com",
                "encoded-password",
                person,
                Role.USER,
                otp,
                expiry
        );
    }



    @Test
    @DisplayName("Should create pending account successfully")
    void shouldCreatePendingAccount() {

        assertEquals("ernest@gmail.com", account.getEmail());
        assertEquals(AccountStatus.AWAITING_CONFIRMATION, account.getStatus());
        assertFalse(account.isConfirmed());
        assertEquals(Role.USER, account.getRole());
    }


    @Test
    @DisplayName("Should confirm email successfully")
    void shouldConfirmEmailSuccessfully() {

        account.confirmEmail("12345");

        assertTrue(account.isConfirmed());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
    }

    @Test
    @DisplayName("Should throw exception for invalid OTP")
    void shouldThrowExceptionForInvalidOtp() {

        ClientException ex = assertThrows(
                ClientException.class,
                () -> account.confirmEmail("99999")
        );

        assertEquals("Invalid OTP", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for expired OTP")
    void shouldThrowExceptionForExpiredOtp() {

        UserAccount expiredAccount = createAccount("12345", LocalDateTime.now().minusMinutes(1));

        OtpExpiredException ex = assertThrows(
                OtpExpiredException.class,
                () -> expiredAccount.confirmEmail("12345")
        );

        assertEquals("OTP has expired", ex.getMessage());
    }


    @Test
    @DisplayName("Should request password reset after confirmation")
    void shouldRequestPasswordResetSuccessfully() {

        account.confirmEmail("12345");

        account.requestPasswordReset("54321");

        assertEquals(AccountStatus.AWAITING_PWD_RESET, account.getStatus());
    }

    @Test
    @DisplayName("Should throw if account not confirmed")
    void shouldThrowIfAccountNotConfirmed() {

        ClientException ex = assertThrows(
                ClientException.class,
                () -> account.requestPasswordReset("54321")
        );

        assertEquals("Please confirm account first", ex.getMessage());
    }


    @Test
    @DisplayName("Should load wallet when account is active")
    void shouldLoadWalletSuccessfully() {

        account.confirmEmail("12345");

        account.loadWallet(BigDecimal.valueOf(200));

        assertEquals(
                BigDecimal.valueOf(200),
                account.getPerson().getWalletBalance()
        );
    }


    @Test
    @DisplayName("Should fail health check for unconfirmed account")
    void shouldFailHealthCheckForUnconfirmedAccount() {

        ClientException ex = assertThrows(
                ClientException.class,
                account::ensureIsHealthy
        );

        assertEquals("Account not yet confirmed", ex.getMessage());
    }

}
