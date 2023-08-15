package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.GoogleCloudStorageInfo;

public interface GoogleCloudStorageInfoRepository extends JpaRepository<GoogleCloudStorageInfo, Long> {

}
