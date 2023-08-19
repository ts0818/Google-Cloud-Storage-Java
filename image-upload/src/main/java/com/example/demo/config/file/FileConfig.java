package com.example.demo.config.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Configuration
@PropertySource(value = { "classpath:/properties/file/file-image.properties" })
@Data
public class FileConfig {

	public static int IMAGE_LIMIT_SIZE_MAXIMUM;
	public static int IMAGE_LIMIT_SIZE_MINIMUM;
	
	public static String[] IMAGE_SIZE_TYPE;
	
	public static int[] IMAGE_SIZE_SMALL_ROW_GREATER_COL;
	public static int[] IMAGE_SIZE_SMALL_ROW_LESS_COL;
	public static int[] IMAGE_SIZE_SMALL_SQUARE;

	public static int[] IMAGE_SIZE_MEDIUM_ROW_GREATER_COL;
	public static int[] IMAGE_SIZE_MEDIUM_ROW_LESS_COL;
	public static int[] IMAGE_SIZE_MEDIUM_SQUARE;

	public static int[] IMAGE_SIZE_LARGE_ROW_GREATER_COL;
	public static int[] IMAGE_SIZE_LARGE_ROW_LESS_COL;
	public static int[] IMAGE_SIZE_LARGE_SQUARE;

	public static int[] IMAGE_SIZE_EXTRA_LARGE_ROW_GREATER_COL;
	public static int[] IMAGE_SIZE_EXTRA_LARGE_ROW_LESS_COL;
	public static int[] IMAGE_SIZE_EXTRA_LARGE_SQUARE;	
	
	public static String MULTIPART_TMP_DIR;
	public static String IMAGE_TMP_DIR;
	
	public FileConfig (@Value("${image.limit.size.maximum}")  int imageLimitSizeMaximum
			,@Value("${image.limit.size.minimum}")  int imageLimitSizeMinimum
			,@Value("${image.size.type}") String[] imageSizeType
			,@Value("${image.size.small.row.greater.col}")  int[] imageSizeSmallRowGreaterCol
			,@Value("${image.size.small.row.less.col}")  int[] imageSizeSmallRowLessCol
			,@Value("${image.size.small.square}")  int[] imageSizeSmallSquare
			,@Value("${image.size.medium.row.greater.col}")  int[] imageSizeMediumRowGreaterCol
			,@Value("${image.size.medium.row.less.col}")  int[] imageSizeMediumRowLessCol
			,@Value("${image.size.medium.square}")  int[] imageSizeMediumSquare
			,@Value("${image.size.large.row.greater.col}")  int[] imageSizeLargeRowGreaterCol
			,@Value("${image.size.large.row.less.col}")  int[] imageSizeLargeRowLessCol
			,@Value("${image.size.large.square}")  int[] imageSizeLargeSquare
			,@Value("${image.size.extra_large.row.greater.col}")  int[] imageSizeExtraLargeRowGreaterCol
			,@Value("${image.size.extra_large.row.less.col}")  int[] imageSizeExtraLargeRowLessCol
			,@Value("${image.size.extra_large.square}")  int[] imageSizeExtraLargeSquare
			,@Value("${multipart.tmp.dir}")  String multipartTemDir
			,@Value("${image.tmp.dir}") String imageTmpDir
			) {
		FileConfig.IMAGE_LIMIT_SIZE_MAXIMUM = imageLimitSizeMaximum;
		FileConfig.IMAGE_LIMIT_SIZE_MINIMUM = imageLimitSizeMinimum;
		FileConfig.IMAGE_SIZE_TYPE = imageSizeType;
		FileConfig.IMAGE_SIZE_SMALL_ROW_GREATER_COL = imageSizeSmallRowGreaterCol;
		FileConfig.IMAGE_SIZE_SMALL_ROW_LESS_COL = imageSizeSmallRowLessCol;
		FileConfig.IMAGE_SIZE_SMALL_SQUARE = imageSizeSmallSquare;
		FileConfig.IMAGE_SIZE_MEDIUM_ROW_GREATER_COL = imageSizeMediumRowGreaterCol;
		FileConfig.IMAGE_SIZE_MEDIUM_ROW_LESS_COL = imageSizeMediumRowLessCol;
		FileConfig.IMAGE_SIZE_MEDIUM_SQUARE = imageSizeMediumSquare;
		FileConfig.IMAGE_SIZE_LARGE_ROW_GREATER_COL = imageSizeLargeRowGreaterCol;
		FileConfig.IMAGE_SIZE_LARGE_ROW_LESS_COL = imageSizeLargeRowLessCol;
		FileConfig.IMAGE_SIZE_LARGE_SQUARE = imageSizeLargeSquare;
		FileConfig.IMAGE_SIZE_EXTRA_LARGE_ROW_GREATER_COL = imageSizeExtraLargeRowGreaterCol;
		FileConfig.IMAGE_SIZE_EXTRA_LARGE_ROW_LESS_COL = imageSizeExtraLargeRowLessCol;
		FileConfig.IMAGE_SIZE_EXTRA_LARGE_SQUARE = imageSizeExtraLargeSquare;
		FileConfig.MULTIPART_TMP_DIR = multipartTemDir;
		FileConfig.IMAGE_TMP_DIR = imageTmpDir;
	}
	
	
}
