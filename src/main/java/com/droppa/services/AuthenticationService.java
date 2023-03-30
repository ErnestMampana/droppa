package com.droppa.clone.droppa.services;

import com.droppa.clone.droppa.common.ClientException;
import com.droppa.clone.droppa.dto.CredentialsDTO;
import com.droppa.clone.droppa.dto.PersonDTO;
import com.droppa.clone.droppa.dto.UserResponseDTO;
import com.droppa.clone.droppa.enums.AccountStatus;
import com.droppa.clone.droppa.enums.Role;
import com.droppa.clone.droppa.enums.TokenType;
import com.droppa.clone.droppa.models.Person;
import com.droppa.clone.droppa.models.Token;
import com.droppa.clone.droppa.models.UserAccount;
import com.droppa.clone.droppa.repositories.PersonRepository;
import com.droppa.clone.droppa.repositories.TokenRepository;
import com.droppa.clone.droppa.repositories.UserAccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

	private final PersonRepository personRepository;
	private final UserAccountRepository userAccountRepository;
	private final PartyService partyService;
	private final TokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final ModelMapper modelMapper;
	private final UserService userService;

	public UserResponseDTO createUserAccount(PersonDTO person) {
		try {
			log.info("===================== Requesting Account create.");
			Optional<UserAccount> userAccount = userAccountRepository.findByEmail(person.getEmail());

			Optional<Person> pers = personRepository.findByEmail(person.getEmail());

			if (userAccount.isPresent())
				throw new ClientException("User already Exist");

			if (pers.isPresent())
				if (pers.get().getEmail().equals(person.getEmail()))
					throw new ClientException("person exist");

			int otp = partyService.generateOTP(person.getEmail());

			Person owner = modelMapper.map(person, Person.class);
			owner.setWalletBalance(0);			

			var acc = UserAccount.builder().email(owner.getEmail()).person(owner).confirmed(false).otp(otp)
					.status(AccountStatus.AWAITING_CONFIRMATION).password(passwordEncoder.encode(person.getPassword()))
					.role(Role.USER).build();

			personRepository.save(owner);

			UserAccount savedUser = userAccountRepository.save(acc);

			log.info("================================= Account created for " + savedUser.getPerson().getUserName()
					+ " " + savedUser.getPerson().getSurname());
			
			var jwtToken = jwtService.generateToken(acc);

			saveUserToken(savedUser, jwtToken);

			UserResponseDTO userResponse = modelMapper.map(owner, UserResponseDTO.class);

			return userResponse;

		} catch (Exception e) {
			throw new ClientException(e.getMessage());
		}
	}

	public UserResponseDTO authenticate(CredentialsDTO request) {

		var user = userService.getUserByEmail(request.getUsername());

		if (user.isConfirmed()) {
			if (user.getStatus().equals(AccountStatus.ACTIVE)) {
				authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

				log.info("Useraccount Logged In ================= " + user.getEmail());
				log.info("Useraccount Logged In ================= " + user.getPerson().getUserName() + " "
						+ user.getPerson().getSurname());

			} else {
				if (user.getStatus().equals(AccountStatus.DELETED)) {
					throw new ClientException(
							"This account has been deleted, please contact Droppa Clone for re-activation.");
				} else if (user.getStatus().equals(AccountStatus.AWAITING_PWD_RESET)) {
					throw new ClientException("User awaiting password reset.");
				} else {
					throw new ClientException(
							"This account has been suspended, please contact Droppa Clone for re-activation.");
				}
			}
		} else {
			throw new ClientException("Account not confirmed");
		}

		var jwtToken = jwtService.generateToken(user);

		revokeAllUserTokens(user);

		saveUserToken(user, jwtToken);

		Person owner = user.getPerson();

		UserResponseDTO userResponseDTO = modelMapper.map(owner, UserResponseDTO.class);

		userResponseDTO.setToken(jwtToken);

		return userResponseDTO;
	}

	private void saveUserToken(UserAccount user, String jwtToken) {
		var token = Token.builder().user(user).token(jwtToken).tokenType(TokenType.BEARER).expired(false).revoked(false)
				.build();
		tokenRepository.save(token);
	}

	private void revokeAllUserTokens(UserAccount user) {
		var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
		if (validUserTokens.isEmpty())
			return;
		validUserTokens.forEach(token -> {
			token.setExpired(true);
			token.setRevoked(true);
		});
		tokenRepository.saveAll(validUserTokens);
	}
}
