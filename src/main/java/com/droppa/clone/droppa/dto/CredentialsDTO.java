/**
 * 
 */
package com.droppa.clone.droppa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ernest Mampana
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialsDTO {
	
	public String username;

	public String password;
}
