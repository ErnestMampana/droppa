package com.droppa.DroppaUserService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.droppa.DroppaUserService.entity.Person;


public interface PersonRepository extends JpaRepository<Person, Integer>{
	
	Optional<Person> findByEmail(String email);

}
