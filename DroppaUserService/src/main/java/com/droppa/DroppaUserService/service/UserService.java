package com.droppa.DroppaUserService.service;

import java.util.List;
import com.droppa.DroppaUserService.repository.UserAccountRepository;
import com.droppa.DroppaUserService.exception.ClientException;
import com.droppa.DroppaUserService.dto.UserResponseDTO;
import com.droppa.DroppaUserService.entity.UserAccount;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserAccountRepository userAccountRepository;

	public List<UserAccount> getAllUsers() {
		return userAccountRepository.findAll();
	}

	

	public UserAccount getUserByEmail(String email) {
		return userAccountRepository.findByEmail(email).
				orElseThrow(() -> new ClientException("Account not found"));
	}
  

	public UserResponseDTO buildUserResponse(UserAccount user,String token) {
		return UserResponseDTO.builder().cellphone(user.getPerson().getCellphone())
				.surname(user.getPerson().getSurname()).userName(user.getPerson().getUserName())
				.token(token).walletBalance(user.getPerson().getWalletBalance())
				.email(user.getEmail()).build();
	}
	

}
