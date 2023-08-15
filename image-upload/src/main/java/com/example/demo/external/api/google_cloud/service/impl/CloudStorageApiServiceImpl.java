package com.example.demo.external.api.google_cloud.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.threeten.bp.Duration;

import com.example.demo.dto.external.google_cloud.GoogleCloudStorageInfoDto;
import com.example.demo.external.api.google_cloud.config.GoogleCloudConfig;
import com.example.demo.external.api.google_cloud.service.CloudStorageApiService;
import com.example.demo.type.UploadStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.retrying.RetrySettings;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.TransportOptions;
import com.google.cloud.WriteChannel;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;

@Service
public class CloudStorageApiServiceImpl implements CloudStorageApiService {
	
	private Storage getStorageServiceObject() {
        // Storageオブジェクトの生成
        Storage storage = null;
        try {
			if("".equals(GoogleCloudConfig.API_KEY_FILE_NAME) != true) {
			    storage = getStorageFromJsonKey(GoogleCloudConfig.API_KEY_FILE_NAME);
			} else {
			    storage = StorageOptions.getDefaultInstance().getService();
			}
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
        return storage;
	}
	
	private static Storage getStorageFromJsonKey(String apiKeyFileName) throws FileNotFoundException, IOException {
        TransportOptions transportOptions = HttpTransportOptions.newBuilder()
                .setConnectTimeout(GoogleCloudConfig.CONNECTION_TIMEOUT)
                .setReadTimeout(GoogleCloudConfig.READ_TIMEOUT)
                .build();

        RetrySettings retrySettings= RetrySettings.newBuilder()
                .setMaxAttempts(GoogleCloudConfig.MAX_ATTEMPTS)
                .setMaxRetryDelay(Duration.ofMillis(GoogleCloudConfig.MAX_RETRY_DELAY))
                .setTotalTimeout(Duration.ofMillis(GoogleCloudConfig.TOTAL_TIMEOUT))
                .setInitialRetryDelay(Duration.ofMillis(GoogleCloudConfig.INITIAL_RETRY_DELAY))
                .setRetryDelayMultiplier(GoogleCloudConfig.RETRY_DELAY_MULTIPLIER)
                .setInitialRpcTimeout(Duration.ofMillis(GoogleCloudConfig.INITIAL_RPC_TIMEOUT))
                .setRpcTimeoutMultiplier(GoogleCloudConfig.RPC_TIMEOUT_MULTIPLIER)
                .setMaxRpcTimeout(Duration.ofMillis(GoogleCloudConfig.MAX_RPC_TIMEOUT))
                .build();

        final GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(GoogleCloudConfig.API_KEY_FILE_NAME))
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

        return StorageOptions.newBuilder()
                .setTransportOptions(transportOptions)
                .setRetrySettings(retrySettings)
				.setCredentials(credentials)
                .build()
                .getService();
	}

	@Override
	public GoogleCloudStorageInfoDto fileUpload(MultipartFile uploadFile) {

		GoogleCloudStorageInfoDto googleCloudStorageInfoDto = new GoogleCloudStorageInfoDto();
		BlobId blobId = null;
		String serverFileName = null;
		int writeByteSize = 0;
		
		try (InputStream inputStream = uploadFile.getInputStream();) {
			// Google Cloud Storageに対して認証
			Storage storage = getStorageServiceObject();
			// Google Cloud Storageのバケットへ保存するファイル名
			serverFileName = createServerFileNameForFileUpload(uploadFile.getOriginalFilename());
			// Google Cloud Storageのバケットで管理するBLOBオブジェクトための識別子を作成
			blobId = BlobId.of(GoogleCloudConfig.STORAGE_BUCKET_NAME, serverFileName);
			// 識別子を元にGoogle Cloud Storageでのバケットで管理するBLOBオブジェクトに関する情報を作成
			BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
	        boolean isUpload = false;

	        // アップロード処理
			try (final WriteChannel uploadWriter = storage.writer(blobInfo);) {
		        // 一度に処理するデータ量
		        byte[] buffer = new byte[10 * 1024 * 1024];
		        int limit;

		        // ファイルのデータが読み込める間は繰り返し
		        while ((limit = inputStream.read(buffer)) >= 0){
		            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, limit);
		            // Google Cloud Storageのバケットへファイルから読み込んだデータを書き込み
		            writeByteSize = uploadWriter.write(byteBuffer);
		            isUpload = true;
		        }
	        } 
			
			//storage.create(blobInfo, Files.readAllBytes(Paths.get(uploadFile.getOriginalFilename())));
			
			// ファイルアップロードが成功した場合
	        if (isUpload && writeByteSize != 0) {
				googleCloudStorageInfoDto.setUploadStatus(UploadStatus.SUCCESS);
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, Object> map = objectMapper.convertValue(blobId, new TypeReference<Map<String, Object>>() {});
				googleCloudStorageInfoDto.setBucketName(String.valueOf(map.get("bucket")));
				googleCloudStorageInfoDto.setBlobName(String.valueOf(map.get("name")));
				if (Objects.nonNull(map.get("generation"))) {
					googleCloudStorageInfoDto.setBlobGeneration(Long.valueOf(String.valueOf(map.get("generation"))));

				}
				googleCloudStorageInfoDto.setFileSize(uploadFile.getSize());
				googleCloudStorageInfoDto.setUploadByteSize(Long.valueOf(writeByteSize) );
	        } else {
	        	googleCloudStorageInfoDto.setUploadStatus(UploadStatus.FAILED);
	        }

		} catch (IOException e) {
			// ファイルアップロードが失敗した場合
			googleCloudStorageInfoDto.setUploadStatus(UploadStatus.FAILED);
			//e.printStackTrace();
		}		
		googleCloudStorageInfoDto.setFileName(serverFileName);

		return googleCloudStorageInfoDto;
	}

	@Override
	public String createServerFileNameForFileUpload(String uploadFileName) {
		
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS");
		
		StringBuilder sb = new StringBuilder();
		sb.append(GoogleCloudConfig.STORAGE_BUCKET_VIRTUAL_SUB_DIR)
		  .append("/")
		  .append(df.format(LocalDateTime.now()))
		  .append("_")
		  .append(uploadFileName);
		return sb.toString();
	}

}
