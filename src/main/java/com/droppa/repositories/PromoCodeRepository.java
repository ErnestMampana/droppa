/**
 * 
 */
package com.droppa.clone.droppa.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.droppa.clone.droppa.models.PromoCode;

/**
 * @author Ernest Mampana
 *
 */
public interface PromoCodeRepository extends JpaRepository<PromoCode, Integer>{
	
	Optional<PromoCode> findByPromoCode(String promoCode);

}
