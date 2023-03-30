package com.droppa.clone.droppa.repositories;

import java.util.Optional;

import com.droppa.clone.droppa.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserAccountRepository extends JpaRepository<UserAccount,Integer>{

	Optional<UserAccount> findByEmail(String email);
}
