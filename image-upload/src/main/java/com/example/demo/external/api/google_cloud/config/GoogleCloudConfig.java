package com.example.demo.external.api.google_cloud.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Configuration
@PropertySource(value = { "classpath:/properties/external/google_cloud/google-cloud.properties" })
@Data
public class GoogleCloudConfig {

	public static String STORAGE_BUCKET_NAME;
	
	public static String STORAGE_BUCKET_VIRTUAL_SUB_DIR;
	
	public static String API_KEY_FILE_NAME;
	
	public static int MAX_ATTEMPTS;
	
	public static int CONNECTION_TIMEOUT;
	
	public static int READ_TIMEOUT;
	
	public static long MAX_RETRY_DELAY;
	
	public static long TOTAL_TIMEOUT;
	
	public static long INITIAL_RETRY_DELAY;
	
	public static double RETRY_DELAY_MULTIPLIER;
	
	public static long INITIAL_RPC_TIMEOUT;
	
	public static double RPC_TIMEOUT_MULTIPLIER;
	
	public static long MAX_RPC_TIMEOUT;
	
	public GoogleCloudConfig (
			@Value("${google.cloud.storage.storage_bucket_name}") String storageBucketName
			, @Value("${google.cloud.storage.storage_bucket_virtual_sub_dir}") String storageBucketVirtualSubDir
			, @Value("${google.cloud.storage.credentials_location}") String apiKeyFileName
			, @Value("${google.cloud.storage.max_attempts}") int maxAttempts 
			, @Value("${google.cloud.storage.connection_timeout}") int connectionTimeOut
			, @Value("${google.cloud.storage.read_timeout}") int readTimeOut
			, @Value("${google.cloud.storage.max_retry_delay}") long maxRetryDelay
			, @Value("${google.cloud.storage.total_timeout}") long totalTimeout
			, @Value("${google.cloud.storage.initial_retry_delay}") long initialRetryDelay
			, @Value("${google.cloud.storage.retry_delay_multiplier}") double retryDelayMultiplier
			, @Value("${google.cloud.storage.initial_rpc_timeout}") long InitialRpcTimeout
			, @Value("${google.cloud.storage.rpc_timeout_multiplier}") double rpcTimeoutMultiplier
			, @Value("${google.cloud.storage.max_rpc_timeout}") long maxRpcTimeout) {
		GoogleCloudConfig.STORAGE_BUCKET_NAME = storageBucketName;
		GoogleCloudConfig.STORAGE_BUCKET_VIRTUAL_SUB_DIR = storageBucketVirtualSubDir;
		GoogleCloudConfig.API_KEY_FILE_NAME = apiKeyFileName;
		GoogleCloudConfig.MAX_ATTEMPTS = maxAttempts;
		GoogleCloudConfig.CONNECTION_TIMEOUT = connectionTimeOut;
		GoogleCloudConfig.READ_TIMEOUT = readTimeOut;
		GoogleCloudConfig.MAX_RETRY_DELAY = maxRetryDelay;
		GoogleCloudConfig.TOTAL_TIMEOUT = totalTimeout;
		GoogleCloudConfig.INITIAL_RETRY_DELAY = initialRetryDelay;
		GoogleCloudConfig.RETRY_DELAY_MULTIPLIER = retryDelayMultiplier;
		GoogleCloudConfig.INITIAL_RPC_TIMEOUT = InitialRpcTimeout;
		GoogleCloudConfig.RPC_TIMEOUT_MULTIPLIER = rpcTimeoutMultiplier;
		GoogleCloudConfig.MAX_RPC_TIMEOUT = maxRpcTimeout;
	}
	
	
}
