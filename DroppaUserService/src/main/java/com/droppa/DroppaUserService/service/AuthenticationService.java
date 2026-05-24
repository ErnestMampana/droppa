package com.droppa.DroppaUserService.service;

import com.droppa.DroppaUserService.exception.ClientException;
import com.droppa.DroppaUserService.dto.CredentialsDTO;
import com.droppa.DroppaUserService.dto.PersonDTO;
import com.droppa.DroppaUserService.dto.UserResponseDTO;
import com.droppa.DroppaUserService.enums.AccountStatus;
import com.droppa.DroppaUserService.enums.Role;
import com.droppa.DroppaUserService.enums.TokenType;
import com.droppa.DroppaUserService.entity.Person;
import com.droppa.DroppaUserService.entity.Token;
import com.droppa.DroppaUserService.entity.UserAccount;
import com.droppa.DroppaUserService.repository.PersonRepository;
import com.droppa.DroppaUserService.repository.TokenRepository;
import com.droppa.DroppaUserService.repository.UserAccountRepository;
import com.droppa.DroppaUserService.security.SecurityUserDetails;
import com.droppa.DroppaUserService.service.JwtService;
import com.droppa.DroppaUserService.service.PartyService;
import com.droppa.DroppaUserService.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

//	public UserResponseDTO createUserAccount(PersonDTO person) {
//		try {
//			log.info("Requesting Account create.");
//
//			userExists(person.getEmail());
//			personExists(person.getEmail());
//
//			String otp = partyService.generateOTP(person.getEmail());
//
//			Person owner = modelMapper.map(person, Person.class);
//			
//			UserAccount user = UserAccount.createPendingAccount(person.getEmail(), person.getPassword(), owner, Role.USER, otp, LocalDateTime.now().plusMinutes(30));
//			
//			personRepository.save(owner);
//
//			userAccountRepository.save(user);
//
//			log.info("Account created for " + user.getPerson().getUserName()
//					+ " " + user.getPerson().getSurname());
//			
//			UserDetails userDetails = new SecurityUserDetails(user);
//
//			var jwtToken = jwtService.generateToken(userDetails);
//			
//
//			saveUserToken(user, jwtToken);
//
//			UserResponseDTO userResponse = modelMapper.map(owner, UserResponseDTO.class);
//
//			return userResponse;
//
//		} catch (Exception e) {
//			throw new ClientException(e.getMessage());
//		}
//	}
	
	@Transactional
	public UserResponseDTO createUserAccount(PersonDTO request) {

	    log.info("Requesting Account create.");

	    userExists(request.getEmail());
	    personExists(request.getEmail());

	    String otp = partyService.generateOTP(request.getEmail());

	    Person owner = Person.create(
	            request.getUserName(),
	            request.getSurname(),
	            request.getCellphone(),
	            request.getEmail()
	    );

	    String encodedPassword =
	            passwordEncoder.encode(request.getPassword());

	    UserAccount user = UserAccount.createPendingAccount(
	            request.getEmail(),
	            encodedPassword,
	            owner,
	            Role.USER,
	            otp,
	            LocalDateTime.now().plusMinutes(30)
	    );

	    userAccountRepository.save(user);

	    log.info(
	            "Account created for {} {}",
	            user.getPerson().getUserName(),
	            user.getPerson().getSurname()
	    );

	    UserDetails userDetails =
	            new SecurityUserDetails(user);

	    String jwtToken =
	            jwtService.generateToken(userDetails);

	    saveUserToken(user, jwtToken);

	    return UserResponseDTO.builder()
	            .userName(user.getPerson().getUserName())
	            .surname(user.getPerson().getSurname())
	            .email(user.getEmail())
	            .cellphone(user.getPerson().getCellphone())
	            .walletBalance(user.getPerson().getWalletBalance())
	            .token(jwtToken)
	            .build();
	}
	


//	public UserResponseDTO authenticate(CredentialsDTO request) {
//
//		var user = userService.getActiveUser(request.getUsername());
//
//		authenticationManager
//				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
//
//		log.info("Useraccount Logged In {} ", user.getEmail());
//		log.info("Useraccount Logged In {} {}", user.getPerson().getUserName(), user.getPerson().getSurname());
//
//		UserDetails userDetails = new SecurityUserDetails(user);
//
//		var jwtToken = jwtService.generateToken(userDetails);
//
//		revokeAllUserTokens(user);
//
//		saveUserToken(user, jwtToken);
//
//		Person owner = user.getPerson();
//
//		UserResponseDTO userResponseDTO = modelMapper.map(owner, UserResponseDTO.class);
//
//		userResponseDTO.setToken(jwtToken);
//
//		return userResponseDTO;
//	}
	
	public UserResponseDTO authenticate(CredentialsDTO request) {

	    authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(
	                    request.getUsername(),
	                    request.getPassword()
	            )
	    );

	    UserAccount user = userService.getUserByEmail(request.getUsername());

	    user.ensureIsHealthy();

	    log.info("User logged in {}", user.getEmail());
	    
	    UserDetails userDetails =
	            new SecurityUserDetails(user);

	    String jwtToken =
	            jwtService.generateToken(userDetails);

	    revokeAllUserTokens(user);
	    saveUserToken(user, jwtToken);

	    return UserResponseDTO.builder()
	            .email(user.getEmail())
	            .userName(user.getPerson().getUserName())
	            .surname(user.getPerson().getSurname())
	            .token(jwtToken)
	            .build();
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
	
	private void userExists(String email) {		
		if(userAccountRepository.findByEmail(email).isPresent()) 
			throw new ClientException("User already exists");
		
		
	}
	
	private void personExists(String email) {
		if (personRepository.findByEmail(email).isPresent())
			throw new ClientException("User already exists");
	}

	
}
