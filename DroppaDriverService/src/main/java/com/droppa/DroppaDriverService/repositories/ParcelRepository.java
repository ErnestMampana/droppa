/**
 * 
 */
package com.droppa.DroppaDriverService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.droppa.DroppaDriverService.entity.Parcel;

/**
 * @author Ernest Mampana
 *
 */

public interface ParcelRepository extends JpaRepository<Parcel, Integer>{

}
