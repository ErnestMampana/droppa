package com.droppa.DroppaUserService.repository;

import java.util.Optional;

import com.droppa.DroppaUserService.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserAccountRepository extends JpaRepository<UserAccount,Integer>{

	Optional<UserAccount> findByEmail(String email);
}
