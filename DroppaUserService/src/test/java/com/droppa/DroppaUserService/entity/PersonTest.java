package com.droppa.DroppaUserService.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.droppa.DroppaUserService.exception.ClientException;

class PersonTest {

    private Person person;

    @BeforeEach
    void setUp() {
        person = Person.create(
                "Ernest",
                "Mohlala",
                "0712345678",
                "ernest@gmail.com"
        );

        person.creditWallet(BigDecimal.valueOf(0));
    }

    @Test
    @DisplayName("Should create person successfully")
    void shouldCreatePersonSuccessfully() {

        Person created = Person.create(
                "Ernest",
                "Mohlala",
                "0821112222",
                "ernest@gmail.com"
        );

        assertNotNull(created);
        assertEquals("Ernest", created.getUserName());
        assertEquals("Mohlala", created.getSurname());
        assertEquals("0821112222", created.getCellphone());
        assertEquals("ernest@gmail.com", created.getEmail());
    }

    @Test
    @DisplayName("Should credit wallet successfully")
    void shouldCreditWalletSuccessfully() {

        person.creditWallet(BigDecimal.valueOf(50));

        assertEquals(
                BigDecimal.valueOf(150),
                person.getWalletBalance()
        );
    }

    @Test
    @DisplayName("Should debit wallet successfully")
    void shouldDebitWalletSuccessfully() {

        person.debit(BigDecimal.valueOf(40));

        assertEquals(
                BigDecimal.valueOf(60),
                person.getWalletBalance()
        );
    }

    @Test
    @DisplayName("Should throw exception when debit amount is null")
    void shouldThrowExceptionWhenDebitAmountIsNull() {

        ClientException exception = assertThrows(
                ClientException.class,
                () -> person.debit(null)
        );

        assertEquals("Invalid amount", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when debit amount is zero")
    void shouldThrowExceptionWhenDebitAmountIsZero() {

        ClientException exception = assertThrows(
                ClientException.class,
                () -> person.debit(BigDecimal.ZERO)
        );

        assertEquals("Invalid amount", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when debit amount is negative")
    void shouldThrowExceptionWhenDebitAmountIsNegative() {

        ClientException exception = assertThrows(
                ClientException.class,
                () -> person.debit(BigDecimal.valueOf(-10))
        );

        assertEquals("Invalid amount", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when balance is insufficient")
    void shouldThrowExceptionWhenBalanceIsInsufficient() {

        ClientException exception = assertThrows(
                ClientException.class,
                () -> person.debit(BigDecimal.valueOf(200))
        );

        assertEquals("Insufficient balance", exception.getMessage());
    }
}