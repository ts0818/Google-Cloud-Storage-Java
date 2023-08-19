package com.example.demo.external.api.google_cloud.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.threeten.bp.Duration;

import com.example.demo.config.file.FileConfig;
import com.example.demo.dto.external.google_cloud.GoogleCloudStorageInfoDto;
import com.example.demo.entity.GoogleCloudStorageInfo;
import com.example.demo.external.api.google_cloud.config.GoogleCloudConfig;
import com.example.demo.external.api.google_cloud.service.CloudStorageApiService;
import com.example.demo.service.impl.GoogleCloudStorageInfoServiceImpl;
import com.example.demo.type.UploadStatus;
import com.example.demo.util.FileUtil;
import com.example.demo.util.concurrent.BarrierActionForFileUpload;
import com.example.demo.util.concurrent.ResizeImageFile;
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

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CloudStorageApiServiceImpl implements CloudStorageApiService {

	@Autowired
	private GoogleCloudStorageInfoServiceImpl googleCloudStorageInfoServiceImpl;

	private static Storage getStorageServiceObject() {
		// Storageオブジェクトの生成
		Storage storage = null;
		try {
			if ("".equals(GoogleCloudConfig.API_KEY_FILE_NAME) != true) {
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
				.setConnectTimeout(GoogleCloudConfig.CONNECTION_TIMEOUT).setReadTimeout(GoogleCloudConfig.READ_TIMEOUT)
				.build();

		RetrySettings retrySettings = RetrySettings.newBuilder().setMaxAttempts(GoogleCloudConfig.MAX_ATTEMPTS)
				.setMaxRetryDelay(Duration.ofMillis(GoogleCloudConfig.MAX_RETRY_DELAY))
				.setTotalTimeout(Duration.ofMillis(GoogleCloudConfig.TOTAL_TIMEOUT))
				.setInitialRetryDelay(Duration.ofMillis(GoogleCloudConfig.INITIAL_RETRY_DELAY))
				.setRetryDelayMultiplier(GoogleCloudConfig.RETRY_DELAY_MULTIPLIER)
				.setInitialRpcTimeout(Duration.ofMillis(GoogleCloudConfig.INITIAL_RPC_TIMEOUT))
				.setRpcTimeoutMultiplier(GoogleCloudConfig.RPC_TIMEOUT_MULTIPLIER)
				.setMaxRpcTimeout(Duration.ofMillis(GoogleCloudConfig.MAX_RPC_TIMEOUT)).build();

		final GoogleCredentials credentials = GoogleCredentials
				.fromStream(new FileInputStream(GoogleCloudConfig.API_KEY_FILE_NAME))
				.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

		return StorageOptions.newBuilder().setTransportOptions(transportOptions).setRetrySettings(retrySettings)
				.setCredentials(credentials).build().getService();
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
				while ((limit = inputStream.read(buffer)) >= 0) {
					ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, limit);
					// Google Cloud Storageのバケットへファイルから読み込んだデータを書き込み
					writeByteSize = uploadWriter.write(byteBuffer);
					isUpload = true;
				}
			}

			// storage.create(blobInfo,
			// Files.readAllBytes(Paths.get(uploadFile.getOriginalFilename())));

			// ファイルアップロードが成功した場合
			if (isUpload && writeByteSize != 0) {
				googleCloudStorageInfoDto.setUploadStatus(UploadStatus.SUCCESS);
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, Object> map = objectMapper.convertValue(blobId, new TypeReference<Map<String, Object>>() {
				});
				googleCloudStorageInfoDto.setBucketName(String.valueOf(map.get("bucket")));
				googleCloudStorageInfoDto.setBlobName(String.valueOf(map.get("name")));
				if (Objects.nonNull(map.get("generation"))) {
					googleCloudStorageInfoDto.setBlobGeneration(Long.valueOf(String.valueOf(map.get("generation"))));

				}
				googleCloudStorageInfoDto.setFileSize(uploadFile.getSize());
				googleCloudStorageInfoDto.setUploadByteSize(Long.valueOf(writeByteSize));
			} else {
				googleCloudStorageInfoDto.setUploadStatus(UploadStatus.FAILED);
			}

		} catch (IOException e) {
			// ファイルアップロードが失敗した場合
			googleCloudStorageInfoDto.setUploadStatus(UploadStatus.FAILED);
			// e.printStackTrace();
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

	@Override
	public GoogleCloudStorageInfoDto fileUploadAfterResize(Map.Entry<String, File> entry, String contentType, String originalFileName, String imageSizeType)
			throws IOException {
		GoogleCloudStorageInfoDto googleCloudStorageInfoDto = new GoogleCloudStorageInfoDto();
		BlobId blobId = null;
		String serverFileName = null;
		int writeByteSize = 0;

		// Google Cloud Storageに対して認証
		Storage storage = getStorageServiceObject();
		// Google Cloud Storageのバケットへ保存するファイル名
		serverFileName = createServerFileNameForResizeImageFileUpload(originalFileName, imageSizeType);
		// Google Cloud Storageのバケットで管理するBLOBオブジェクトための識別子を作成
		blobId = BlobId.of(GoogleCloudConfig.STORAGE_BUCKET_NAME, serverFileName);
		// 識別子を元にGoogle Cloud Storageでのバケットで管理するBLOBオブジェクトに関する情報を作成
//		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		boolean isUpload = false;

		InputStream inputStream = new FileInputStream(entry.getValue());
		// アップロード処理
		try (final WriteChannel uploadWriter = storage.writer(blobInfo);) {
			// 一度に処理するデータ量
			byte[] buffer = new byte[10 * 1024 * 1024];
			int limit;

			// ファイルのデータが読み込める間は繰り返し
			while ((limit = inputStream.read(buffer)) >= 0) {
				ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, limit);
				// Google Cloud Storageのバケットへファイルから読み込んだデータを書き込み
				writeByteSize = uploadWriter.write(byteBuffer);
				isUpload = true;
			}
		}
		// ファイルアップロードが成功した場合
		if (isUpload && writeByteSize != 0) {
			googleCloudStorageInfoDto.setUploadStatus(UploadStatus.SUCCESS);
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> map = objectMapper.convertValue(blobId, new TypeReference<Map<String, Object>>() {
			});
			googleCloudStorageInfoDto.setBucketName(String.valueOf(map.get("bucket")));
			googleCloudStorageInfoDto.setBlobName(String.valueOf(map.get("name")));
			if (Objects.nonNull(map.get("generation"))) {
				googleCloudStorageInfoDto.setBlobGeneration(Long.valueOf(String.valueOf(map.get("generation"))));

			}
			googleCloudStorageInfoDto.setFileSize(Long.valueOf(writeByteSize));
			googleCloudStorageInfoDto.setUploadByteSize(Long.valueOf(writeByteSize));
		} else {
			googleCloudStorageInfoDto.setUploadStatus(UploadStatus.FAILED);
		}
		googleCloudStorageInfoDto.setFileName(serverFileName);
		return googleCloudStorageInfoDto;
	}

	public void executeCyclicBarrierForImageResizeAndUpload(MultipartFile uploadFile)
			throws IllegalStateException, IOException {

//		final String[] fileNameInfoArr = uploadFile.getOriginalFilename().split("\\.");
//		final String fileExtention = fileNameInfoArr[fileNameInfoArr.length -1];

		// スレッドで共有できるように一時ファイルを作成
		final Path tmpFilePath = Paths.get(FileConfig.MULTIPART_TMP_DIR, uploadFile.getOriginalFilename());
		File tmpFile = new File(tmpFilePath.toString());
		uploadFile.transferTo(tmpFile);

		List<GoogleCloudStorageInfoDto> googleCloudStorageInfoDtoList = new ArrayList<>();
		ConcurrentHashMap<String, File> concurrentHashMap = new ConcurrentHashMap<String, File>();
		BarrierActionForFileUpload barrierActionForFileUpload = new BarrierActionForFileUpload(
				new CloudStorageApiServiceImpl()
				, concurrentHashMap
				, googleCloudStorageInfoDtoList
				, uploadFile.getContentType()
				, uploadFile.getOriginalFilename());

		final int executorCount = FileConfig.IMAGE_SIZE_TYPE.length;
		CyclicBarrier barrier = new CyclicBarrier(executorCount, barrierActionForFileUpload);

		ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
		concurrentLinkedQueue.addAll(Arrays.asList(FileConfig.IMAGE_SIZE_TYPE));

//		ExecutorService executorService = Executors.newFixedThreadPool(executorCount);
		ExecutorService executorService = Executors.newCachedThreadPool();

		Future<?> future = null;
		try {
//			Future<?> future = executorService.submit(resizeImageFile);
			// executorService.shutdown();

//			if (future.isDone()) {
////				for (GoogleCloudStorageInfoDto googleCloudStorageInfoDto: BarrierActionForFileUpload.googleCloudStorageInfoDtoList) {
//					GoogleCloudStorageInfo googleCloudStorageInfo = new GoogleCloudStorageInfo();
//					BeanUtils.copyProperties(future.get(), googleCloudStorageInfo);
//					googleCloudStorageInfoServiceImpl.save(googleCloudStorageInfo);
////				}
//			}

			for (String imageType : FileConfig.IMAGE_SIZE_TYPE) {

				future = executorService.submit(new ResizeImageFile(tmpFile, uploadFile.getContentType(),
						GoogleCloudConfig.STORAGE_BUCKET_VIRTUAL_SUB_DIR, concurrentLinkedQueue, imageType,
						concurrentHashMap, barrier));

				// Object obj = future.get();

			}
			// ExecutorServiceのsubmitの処理が完了するまで待機する
			Object obj = future.get();
			log.info(obj);

		} catch (Exception e) {
			log.error(e);	

		} finally {
			executorService.shutdown();

		}

		// 結果をデータベースに保存する
		if (future.isDone()) {
			for (GoogleCloudStorageInfoDto googleCloudStorageInfoDto : barrierActionForFileUpload
					.getGoogleCloudStorageInfoDtoList()) {
				GoogleCloudStorageInfo googleCloudStorageInfo = new GoogleCloudStorageInfo();
				BeanUtils.copyProperties(googleCloudStorageInfoDto, googleCloudStorageInfo);
				googleCloudStorageInfoServiceImpl.save(googleCloudStorageInfo);

			}

		} else if (future.isCancelled()) {
			log.error("Failed Resize and Upload.");
		}		
		// 一時ファイルを削除
		boolean isDelete = FileUtil.deleteTmpFile(Paths.get(FileConfig.IMAGE_TMP_DIR, "upload", "api").toString());
		log.info("Success Resize and Upload.", isDelete);
	}
	
	@Override
	public String createServerFileNameForResizeImageFileUpload(String uploadFileName, String imageSizeType) {

		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS");

		StringBuilder sb = new StringBuilder();
		sb.append(GoogleCloudConfig.STORAGE_BUCKET_VIRTUAL_SUB_DIR)
		    .append("/")
		    .append(df.format(LocalDateTime.now()))
		    .append("_")
		    .append(imageSizeType)
		    .append("_")
		    .append(uploadFileName);
		return sb.toString();
	}

}
