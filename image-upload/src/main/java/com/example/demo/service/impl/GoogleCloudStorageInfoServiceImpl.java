package com.example.demo.service.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.GoogleCloudStorageInfo;
import com.example.demo.repository.GoogleCloudStorageInfoRepository;
import com.example.demo.service.GoogleCloudStorageInfoService;

@Service
public class GoogleCloudStorageInfoServiceImpl implements GoogleCloudStorageInfoService {

	@Autowired
	private GoogleCloudStorageInfoRepository googleCloudStorageInfoRepository;
	
	//@Transactional
	@Override
	public int save(GoogleCloudStorageInfo entity) {
		GoogleCloudStorageInfo googleCloudStorageInfo = googleCloudStorageInfoRepository.save(entity);
		if (Objects.nonNull(googleCloudStorageInfo)) {
			return 1;
		}
		return 0;
	}

}
