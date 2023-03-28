/**
 *
 */
package com.droppa.clone.droppa.models;

import com.droppa.clone.droppa.enums.RentalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author Ernest Mampana
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Rental {

    @Id
    @GeneratedValue
    private int id;
    private String rentalId;
    private String userId;
    private String streetAddress;
    private int postalCode;
    private String suburb;
    private String province;
    private String complexName;
    private int unitNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String truckType;
    private double price;
    private String companyName;
    private String contactPerson;
    private String mobileNumber;
    private String rentalBrunch;
    private int labours;
    private int noDays;
    private String instruction;
    @Enumerated(EnumType.STRING)
    private RentalStatus status;
    private String promoCodeUsed;
    private String paymentType;

}
