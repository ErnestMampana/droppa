/**
 * 
 */
package com.droppa.DroppaBookingService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.droppa.DroppaBookingService.entity.PromoCode;

/**
 * @author Ernest Mampana
 *
 */
public interface PromoCodeRepository extends JpaRepository<PromoCode, Integer>{
	
	Optional<PromoCode> findByPromoCode(String promoCode);

}
