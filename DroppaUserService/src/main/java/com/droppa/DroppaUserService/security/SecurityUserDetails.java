package com.droppa.DroppaUserService.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.droppa.DroppaUserService.entity.UserAccount;
import com.droppa.DroppaUserService.enums.AccountStatus;

public class SecurityUserDetails implements UserDetails{

	private final UserAccount userAccount;

    public SecurityUserDetails(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(
                        userAccount.getRole().name()
                )
        );
    }

    @Override
    public String getPassword() {
    	return userAccount.getEncodedPassword();
    }

    @Override
    public String getUsername() {
        return userAccount.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return userAccount.getStatus() != AccountStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userAccount.isConfirmed();
    }

}
