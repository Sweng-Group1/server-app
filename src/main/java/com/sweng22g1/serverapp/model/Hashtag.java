package com.sweng22g1.serverapp.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sidharth Shanmugam
 *
 *         The Hashtag entity which is defined as a table in the database by
 *         Spring Boot and JPA.
 *
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Hashtag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Name cannot be null")
	@Size(max = 100, min = 1, message = "Name length invalid")
	@Column(unique = true)
	private String name;
	
	@DecimalMin(value = "-90", inclusive = true, message = "Latitude value invalid - less than -90")
	@DecimalMax(value = "90", inclusive = true, message = "Latitude value invalid - greater than 90")
	private Double latitude;

	@DecimalMin(value = "-180", inclusive = true, message = "Latitude value invalid - less than -180")
	@DecimalMax(value = "180", inclusive = true, message = "Latitude value invalid - greater than 180")
	private Double longitude;

	@Override
	public String toString() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(this.getId()));
		params.put("name", this.getName());
		params.put("latitude", String.valueOf(this.getLatitude()));
		params.put("longitude", String.valueOf(this.getLongitude()));
		return params.toString();
	}
}
