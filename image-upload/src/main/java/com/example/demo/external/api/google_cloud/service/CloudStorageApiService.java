package com.example.demo.external.api.google_cloud.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.external.google_cloud.GoogleCloudStorageInfoDto;

public interface CloudStorageApiService {

	public GoogleCloudStorageInfoDto fileUpload(MultipartFile uploadFileName);
	
	public String createServerFileNameForFileUpload(String uploadFileName);
}
