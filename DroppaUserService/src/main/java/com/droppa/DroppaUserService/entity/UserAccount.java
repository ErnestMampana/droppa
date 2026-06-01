package com.droppa.DroppaUserService.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.droppa.DroppaUserService.enums.AccountStatus;
import com.droppa.DroppaUserService.enums.Role;
import com.droppa.DroppaUserService.exception.ClientException;
import com.droppa.DroppaUserService.security.CredentialManager;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "UserAccount")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String email;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "person_id")
    private Person person;

    private boolean confirmed;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private CredentialManager credentials;

    public static UserAccount createPendingAccount(
            String email,
            String encodedPassword,
            Person person,
            Role role,
            String otp,
            LocalDateTime expiry
    ) {

        UserAccount user = new UserAccount();

        user.email = email;
        user.person = person;
        user.role = role;

        user.credentials = new CredentialManager(encodedPassword);
        user.credentials.requestPasswordReset(otp);

        user.status = AccountStatus.AWAITING_CONFIRMATION;
        user.confirmed = false;

        return user;
    }

    public void confirmEmail(String otpCode) {

        if (!credentials.isOtpValid(otpCode)) {
            throw new ClientException("Invalid OTP");
        }

        credentials.clearOtp();

        this.confirmed = true;
        this.status = AccountStatus.ACTIVE;
    }

    public void requestPasswordReset(String otp) {

        ensureCanModifyAccount();

        credentials.requestPasswordReset(otp);

        this.status = AccountStatus.AWAITING_PWD_RESET;
    }

    public void resetPassword(String otp, String password) {
        credentials.resetPassword(otp, password);
        this.status = AccountStatus.ACTIVE;
    }

    public void loadWallet(BigDecimal amount) {

        ensureCanModifyAccount();

        person.creditWallet(amount);
    }

    private void ensureCanModifyAccount() {

        if (status == AccountStatus.SUSPENDED) {
            throw new ClientException("Account suspended");
        }

        if (status == AccountStatus.AWAITING_CONFIRMATION) {
            throw new ClientException("Please confirm account first");
        }

        if (status == AccountStatus.AWAITING_PWD_RESET) {
            throw new ClientException("Password reset pending");
        }
    }
    
    public String getEncodedPassword() {
        return credentials.getPassword();
    }
    
    public void ensureIsHealthy() {

        if (status == AccountStatus.SUSPENDED) {
            throw new ClientException("Account is suspended");
        }

        if (status == AccountStatus.DELETED) {
            throw new ClientException("Account has been deleted");
        }

        if (status == AccountStatus.AWAITING_CONFIRMATION) {
            throw new ClientException("Account not yet confirmed");
        }

        if (status == AccountStatus.AWAITING_PWD_RESET) {
            throw new ClientException("Password reset in progress");
        }

        if (!confirmed || status != AccountStatus.ACTIVE) {
            throw new ClientException("Account is not active");
        }
    }
}