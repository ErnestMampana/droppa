package com.droppa.DroppaUserService.security;

import java.time.LocalDateTime;
import java.util.Objects;

import com.droppa.DroppaUserService.enums.AccountStatus;
import com.droppa.DroppaUserService.exception.ClientException;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CredentialManager {

    private String password;

    private String otp;

    private LocalDateTime otpExpiry;

    public CredentialManager(String password) {
        this.password = password;
    }

    public void requestPasswordReset(String otp) {

        this.otp = otp;
        this.otpExpiry = LocalDateTime.now().plusMinutes(5);
    }

    public void resetPassword(String otp, String encodedPassword) {

        if (!isOtpValid(otp)) {
            throw new ClientException("Invalid OTP");
        }

        this.password = encodedPassword;
        clearOtp();
    }

    public boolean isOtpValid(String otpCode) {

        if (otp == null || !otp.equals(otpCode)) {
            return false;
        }

        return !otpExpiry.isBefore(LocalDateTime.now());
    }

    public void clearOtp() {
        this.otp = null;
        this.otpExpiry = null;
    }
}