package com.example.demo.external.api.google_cloud.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.external.google_cloud.GoogleCloudStorageInfoDto;

public interface CloudStorageApiService {

	public GoogleCloudStorageInfoDto fileUpload(MultipartFile uploadFile);
	
	public String createServerFileNameForFileUpload(String uploadFileName);

	public GoogleCloudStorageInfoDto fileUploadAfterResize(Map.Entry<String, File> entry, String contentType, String originalFileName, String imageSizeType) throws IOException;
	
	public String createServerFileNameForResizeImageFileUpload(String uploadFileName, String imageSizeType);
}
