package com.example.demo.entity;

import org.hibernate.annotations.Type;

import com.example.demo.type.UploadStatus;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="google_cloud_storage_info")
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
public class GoogleCloudStorageInfo extends BaseEntity {

	@Id
	@SequenceGenerator(name = "google_cloud_storage_info_id_gen"
	, sequenceName = "google_cloud_storage_info_id_seq"
	, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.IDENTITY
	, generator = "google_cloud_storage_info_id_gen")
	@Column(name="id" 
	        , nullable = false
			, columnDefinition = "SIRIAL")
	private Long id;
	
	@Column(name="bucket_name"
			, columnDefinition = "VARCHAR(255)")
	private String bucketName;
	
	@Column(name="blob_name"
			, columnDefinition = "VARCHAR(1024)")
	private String blobName;
	
	@Column(name="blob_generation"
			, columnDefinition = "BIGINT")
	private Long blobGeneration;

	@Column(name="file_name"
			, columnDefinition = "VARCHAR(1024)")
	private String fileName;
	
	@Column(name="upload_status"
			, nullable = false
			, columnDefinition = "upload_status"
	)
	@Enumerated(EnumType.STRING)
	@Type(PostgreSQLEnumType.class)
	//@ColumnTransformer(write = "?::upload_status")
	private UploadStatus uploadStatus;
	
	@Column(name="file_size"
			, columnDefinition = "BIGINT"
	)
	private Long fileSize;
	
	@Column(name="upload_byte_size"
			, columnDefinition = "BIGINT"
	)
	private Long uploadByteSize;
	
	@Column(name="delete_flg"
			, columnDefinition = "BOOLEAN")
	private boolean deleteFlg; 
}
