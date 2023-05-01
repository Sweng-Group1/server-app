package com.sweng22g1.serverapp.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sidharth Shanmugam
 * 
 *         The Media entity which is defined as a table in the database by
 *         Spring Boot and JPA.
 *
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Media {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Filepath cannot be null")
	@Size(max = 255, min = 1, message = "Filepath length invalid")
	@Column(unique = true)
	private String filepath;

	@NotNull(message = "MIMEType cannot be null")
	@Size(max = 100, min = 1, message = "MIMEType length invalid.")
	private String mimetype;

	@Override
	public String toString() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(this.getId()));
		params.put("filepath", this.getFilepath());
		params.put("mimetype", this.getMimetype());
		return params.toString();
	}
}
