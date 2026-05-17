/**
 * 
 */
package com.droppa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.droppa.models.Parcel;

/**
 * @author Ernest Mampana
 *
 */

public interface ParcelRepository extends JpaRepository<Parcel, Integer>{

}
