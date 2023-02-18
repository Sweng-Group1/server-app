package com.sweng22g1.serverapp.model;

import java.util.HashMap;

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

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Map {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "Map name cannot be null")
	@Size(max = 100, min = 1, message = "Map name length invalid.")
	@Column(unique = true)
	private String name;
	
	@NotNull(message = "Map filepath cannot be null")
	@Size(max = 255, min = 1, message = "Map filepath length invalid")
	@Column(unique = true)
	private String filepath;
	
	@Override
	public String toString() {
		java.util.Map<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(this.getId()));
		params.put("name", this.getName());
		params.put("filepath", this.getFilepath());
		return params.toString();
	}
	
}
