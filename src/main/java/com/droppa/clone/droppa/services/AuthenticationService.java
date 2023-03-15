package com.droppa.clone.droppa.services;

import com.droppa.clone.droppa.auth.AuthenticationResponse;
import com.droppa.clone.droppa.common.ClientException;
import com.droppa.clone.droppa.dto.AuthenticationRequest;
import com.droppa.clone.droppa.dto.CredentialsDTO;
import com.droppa.clone.droppa.dto.PersonDTO;
import com.droppa.clone.droppa.dto.RegisterRequest;
import com.droppa.clone.droppa.enums.AccountStatus;
import com.droppa.clone.droppa.enums.Role;
import com.droppa.clone.droppa.enums.TokenType;
import com.droppa.clone.droppa.models.Person;
import com.droppa.clone.droppa.models.Token;
import com.droppa.clone.droppa.models.UserAccount;
import com.droppa.clone.droppa.repositories.PersonRepository;
import com.droppa.clone.droppa.repositories.TokenRepository;
import com.droppa.clone.droppa.repositories.UserAccountRepository;
import com.droppa.clone.droppa.services.JwtService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	
	private final PersonRepository personRepository;
	private final UserAccountRepository userAccountRepository;
	private final PartyService partyService;
	private final TokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthenticationResponse createUserAccount(PersonDTO person) {

		Optional<UserAccount> userAccount = userAccountRepository.findByEmail(person.getEmail());

		Optional<Person> pers = personRepository.findByEmail(person.getEmail());

		if (userAccount.isPresent())
			throw new ClientException("User already Exist");

		if (pers.isPresent()) 
			if (pers.get().getEmail().equals(person.getEmail()))
				throw new ClientException("person exist");
		

		int otp = partyService.generateOTP(person.getCellphone());

		var owner = Person.builder().userName(person.getUserName()).surname(person.getSurname())
				.cellphone(person.getCellphone()).walletBalance(00.0).email(person.getEmail()).build();

		var acc = UserAccount.builder().email(owner.getEmail()).person(owner).confirmed(false).otp(otp)
				.status(AccountStatus.AWAITING_CONFIRMATION).password(passwordEncoder.encode(person.getPassword()))
				.role(Role.USER).build();

		personRepository.save(owner);

		UserAccount savedUser = userAccountRepository.save(acc);

		var jwtToken = jwtService.generateToken(acc);

		saveUserToken(savedUser, jwtToken);

		System.out.println(jwtToken);

		return AuthenticationResponse.builder().token(jwtToken).build();
	}

	public AuthenticationResponse authenticate(CredentialsDTO request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		
		var user = userAccountRepository.findByEmail(request.getUsername()).orElseThrow();
		
		var jwtToken = jwtService.generateToken(user);
		
		revokeAllUserTokens(user);
		
		saveUserToken(user, jwtToken);
		
		return AuthenticationResponse.builder().token(jwtToken).build();
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
