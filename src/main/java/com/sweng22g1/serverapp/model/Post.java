package com.sweng22g1.serverapp.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sidharth Shanmugam
 * 
 *         The Post entity which is defined as a table in the database by Spring
 *         Boot and JPA.
 *
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "xmlContent cannot be null")
	// TODO: Determine sensible limit for XML length.
	@Size(max = 1000, min = 1, message = "xmlContent length invalid.")
//	@Column(unique = true)
	private String xmlContent;

	@CreationTimestamp
	private LocalDateTime created;

	@Past(message = "Updated timestamp must be in the past.")
	private LocalDateTime updated;

	@Future(message = "Expiry timestamp must be in the future.")
	private LocalDateTime expiry;

	@DecimalMin(value = "-90", inclusive = true, message = "Latitude value invalid - less than -90")
	@DecimalMax(value = "90", inclusive = true, message = "Latitude value invalid - greater than 90")
	private Double latitude;

	@DecimalMin(value = "-180", inclusive = true, message = "Latitude value invalid - less than -180")
	@DecimalMax(value = "180", inclusive = true, message = "Latitude value invalid - greater than 180")
	private Double longitude;

	@ManyToOne
	private Hashtag hashtag;

	@Override
	public String toString() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(this.getId()));
		params.put("created", String.valueOf(this.getCreated()));
		params.put("updated", String.valueOf(this.getUpdated()));
		params.put("expiry", String.valueOf(this.getExpiry()));
		params.put("latitude", String.valueOf(this.getLatitude()));
		params.put("longitude", String.valueOf(this.getLongitude()));
		params.put("xmlContent", this.getXmlContent());
		if (this.getHashtag() == null) {
			params.put("hashtag", "null");
		} else {
			params.put("hashtag", this.getHashtag().toString());
		}
		return params.toString();
	}

}
