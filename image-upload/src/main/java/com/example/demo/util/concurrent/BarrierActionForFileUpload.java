package com.example.demo.util.concurrent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.demo.config.file.FileConfig;
import com.example.demo.dto.external.google_cloud.GoogleCloudStorageInfoDto;
import com.example.demo.external.api.google_cloud.service.impl.CloudStorageApiServiceImpl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class BarrierActionForFileUpload implements Runnable {

	private CloudStorageApiServiceImpl cloudStorageApiServiceImpl;
	
	private ConcurrentHashMap<String, File> resizeImageFileDataByteMap;

	public List<GoogleCloudStorageInfoDto> googleCloudStorageInfoDtoList;
	
	private String contentType;
	
	private String originalFileName;
	
	@Override
	public void run() {
		// 
		String imageType = "";
		for (Map.Entry<String, File> entry: this.resizeImageFileDataByteMap.entrySet()) {
			try {
				
				if (isAlreadyUpload(entry.getKey(), this.googleCloudStorageInfoDtoList)) {
					continue;
				}
				
				if (imageType.length() == 0 
						|| !entry.getKey().contains(imageType)) {
					GoogleCloudStorageInfoDto googleCloudStorageInfoDto = cloudStorageApiServiceImpl.fileUploadAfterResize(entry
							, this.contentType
							, this.originalFileName
							, imageType(entry.getKey())
					);
					this.googleCloudStorageInfoDtoList.add(googleCloudStorageInfoDto) ;
					imageType = imageType(entry.getKey());
				}			
				
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

	}
	
	public static String imageType(String fileName) {
		for (String imageType: FileConfig.IMAGE_SIZE_TYPE) {
			if (fileName.contains(imageType)) {
				return imageType;
			}
		}
		return "";
	}
	
	public static boolean isAlreadyUpload(String fileName,  List<GoogleCloudStorageInfoDto> googleCloudStorageInfoDtoList) {

		for (GoogleCloudStorageInfoDto googleCloudStorageInfoDto: googleCloudStorageInfoDtoList) {
			String imageTypeForBlobName = imageType(googleCloudStorageInfoDto.getBlobName());
			String imageTypeForFileName = imageType(fileName);
			if(imageTypeForBlobName.equals(imageTypeForFileName)) {
				return true;
			}
		}		
		return false;
	}

}
