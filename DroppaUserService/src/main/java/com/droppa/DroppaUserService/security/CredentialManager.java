package com.droppa.DroppaUserService.security;

import java.time.LocalDateTime;

import com.droppa.DroppaUserService.exception.ClientException;
import com.droppa.DroppaUserService.exception.OtpExpiredException;

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
        setOtp(otp, LocalDateTime.now().plusMinutes(5));
    }

    public void setOtp(String otp, LocalDateTime otpExpiry) {
        this.otp = otp;
        this.otpExpiry = otpExpiry;
    }

    public void resetPassword(String otp, String encodedPassword) {

        validateOtp(otp);

        this.password = encodedPassword;
        clearOtp();
    }

    public void validateOtp(String otpCode) {

        if (otp == null || !otp.equals(otpCode)) {
            throw new ClientException("Invalid OTP");
        }

        if (otpExpiry == null || otpExpiry.isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException();
        }
    }

    public void clearOtp() {
        this.otp = null;
        this.otpExpiry = null;
    }

}
