package com.example.demo.dto.external.google_cloud;

import com.example.demo.type.UploadStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleCloudStorageInfoDto {

	private String bucketName;
	private String blobName;
	private Long blobGeneration;
	private String fileName;
	private UploadStatus uploadStatus;
	private Long fileSize;
	private Long uploadByteSize;
}
