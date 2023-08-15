package com.example.demo.entity;

import java.io.Serializable;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

@MappedSuperclass
@Data
public class BaseEntity implements Serializable {

	@Column(name="created_time"
			, nullable = false
			, updatable = false 
			//, insertable = false
			, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
	private OffsetDateTime createdTime;
	
	@Column(name="updated_time"
			, insertable = false
			//, updatable = false
			, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private OffsetDateTime updatedTime;
	
	@PrePersist
	public void onPrePersist() {
		//setCreatedTime(OffsetDateTime.now());
		this.createdTime = OffsetDateTime.now();
	}
	
	@PreUpdate
	public void onPreUpdate() {
		//setUpdatedTime(OffsetDateTime.now());
		this.updatedTime = OffsetDateTime.now();
	}
	
}
