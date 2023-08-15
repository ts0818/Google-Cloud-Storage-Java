package com.example.demo.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.external.google_cloud.GoogleCloudStorageInfoDto;
import com.example.demo.entity.GoogleCloudStorageInfo;
import com.example.demo.external.api.google_cloud.service.impl.CloudStorageApiServiceImpl;
import com.example.demo.service.impl.GoogleCloudStorageInfoServiceImpl;

@RestController
@RequestMapping("api/google-cloud/storage")
public class GoogleCloudStrageApiController {

	@Autowired
	private GoogleCloudStorageInfoServiceImpl googleCloudStorageInfoServiceImpl;
	
	@Autowired
	private CloudStorageApiServiceImpl cloudStorageApiServiceImpl;
	
	@PostMapping("upload")
	public boolean uploadFile(@RequestParam("upload_file") MultipartFile uploadFile) {
		GoogleCloudStorageInfoDto  googleCloudStorageInfoDto  = cloudStorageApiServiceImpl.fileUpload(uploadFile);
		GoogleCloudStorageInfo googleCloudStorageInfo = new GoogleCloudStorageInfo();
		BeanUtils.copyProperties(googleCloudStorageInfoDto, googleCloudStorageInfo);
		int result = googleCloudStorageInfoServiceImpl.save(googleCloudStorageInfo);
		return result != 0 ? true: false;
	}
	
}
