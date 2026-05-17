package com.droppa.repositories;

import java.util.Optional;

import com.droppa.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PersonRepository extends JpaRepository<Person, Integer>{
	
	Optional<Person> findByEmail(String email);

}
