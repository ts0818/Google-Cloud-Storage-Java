package com.example.demo.util;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.opencv.imgcodecs.Imgcodecs;

import com.example.demo.config.file.FileConfig;
import com.example.demo.type.file.ImageFileSizeType;
import com.example.demo.type.file.MethodForResize;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class FileUtil {

	/**
	 * MultipartFileからMatへ変換
	 * @param uploadFile アップロードファイル
	 * @return
	 * @throws IOException
	 */
	public static Mat readImage(File tmpFile) throws IOException {
		Mat mat = Mat.EMPTY;
		try (InputStream inputStream = new FileInputStream(tmpFile)) {
			byte[] fileData = new byte[10*1024*1024];
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			while ((nRead = inputStream.read(fileData)) >= 0) {
			    buffer.write(fileData, 0, nRead);
			}
			byte[] bytes = buffer.toByteArray();
			mat = imdecode(new Mat(bytes), Imgcodecs.IMREAD_UNCHANGED);
		}
		return mat;
	}
	
	/**
	 * 画像の横幅と縦幅を取得する
	 * @param mat
	 * @return
	 */
	public static Point takeImageSize(Mat mat) {
//		UByteIndexer srcIndexer = mat.createIndexer();
//        long rows = srcIndexer.sizes()[0]; // 横幅
//        long cols = srcIndexer.sizes()[1]; // 縦幅
        
        int x = mat.cols(); // 横幅
        int y = mat.rows(); // 縦幅
        
//        Point point = new Point(Long.valueOf(rows).intValue(), Long.valueOf(cols).intValue());
        Point point = new Point(x, y);

        return point;
	}
	
	/**
	 * 拡大・縮小の判定
	 * @param point 画像の横幅と縦幅
	 * @return
	 */
	public static MethodForResize selectMethodForResizeImage (Point point) {
		int maxSize = Math.max(point.x, point.y);
		
		// 縮小のみ
		if (FileConfig.IMAGE_LIMIT_SIZE_MAXIMUM < maxSize) {
			
			if (point.x > point.y) {
				return MethodForResize.MINIMIZE_FROM_ROW; // 縮小（横幅を起点）

			} else if (point.x < point.y) {
				return MethodForResize.MINIMIZE_FROM_COL; // 縮小（縦幅を起点）

			} else {
				return MethodForResize.MINIMIZE_BY_SQUARE; // 縮小（正方形）

			}
		
		// 拡大のみ
		} else if (FileConfig.IMAGE_LIMIT_SIZE_MINIMUM > maxSize) {
			if (point.x > point.y) {
				return MethodForResize.ENLARGE_FROM_ROW; // 拡大（横幅を起点）

			} else if (point.x < point.y) {
				return MethodForResize.ENLARGE_FROM_COL; // 拡大（縦幅を起点）
				
			} else {
				return MethodForResize.ENLARGE_BY_SQUARE; // 拡大（正方形）

			}
		
		// 縮小または拡大
		} else {
			if (point.x > point.y) {
				return MethodForResize.BOTH_FROM_ROW; // 拡大または縮小（横幅を起点）

			} else if (point.x < point.y) {
				return MethodForResize.BOTH_FROM_COL; // 拡大または縮小（縦幅を起点）
				
			} else {
				return MethodForResize.BOTH_BY_SQUARE; // 拡大または縮小（正方形）

			}
		}
	}
	
	public static Point takeResizePoint (String imageType, MethodForResize methodForResize, Point point) {

		ImageFileSizeType imageFileSizeType = ImageFileSizeType.getByProperty(imageType);
		
		switch (imageFileSizeType) {
		case SMALL:
			return takeResizePointByMethodForResizeSmall(methodForResize, point);

		case MEDIUM:
			return takeResizePointByMethodForResizeMedium(methodForResize, point);

		case LARGE:
			return takeResizePointByMethodForResizeLarge(methodForResize, point);
			
		case EXTRA_LARGE:
			return takeResizePointByMethodForResizeExtraLarge(methodForResize, point);
			
		default:
			return point;
		}
		
	}
	
	public static Point takeResizePointByMethodForResizeSmall (MethodForResize methodForResize
			, Point point) {
		switch (methodForResize) {
		case MINIMIZE_FROM_ROW:
			return new Point(FileConfig.IMAGE_SIZE_SMALL_ROW_GREATER_COL[0]
					,FileConfig.IMAGE_SIZE_SMALL_ROW_GREATER_COL[1]);

		case MINIMIZE_FROM_COL:
			return new Point(FileConfig.IMAGE_SIZE_SMALL_ROW_LESS_COL[0]
					,FileConfig.IMAGE_SIZE_SMALL_ROW_LESS_COL[1]);
			
		case MINIMIZE_BY_SQUARE:
			return new Point(FileConfig.IMAGE_SIZE_SMALL_SQUARE[0]
					,FileConfig.IMAGE_SIZE_SMALL_SQUARE[1]);

		case ENLARGE_FROM_ROW:
			return new Point(FileConfig.IMAGE_SIZE_SMALL_ROW_GREATER_COL[0]
					,FileConfig.IMAGE_SIZE_SMALL_ROW_GREATER_COL[1]);
			
		case ENLARGE_FROM_COL:
			return new Point(FileConfig.IMAGE_SIZE_SMALL_ROW_LESS_COL[0]
					,FileConfig.IMAGE_SIZE_SMALL_ROW_LESS_COL[1]);

		case ENLARGE_BY_SQUARE:
			return new Point(FileConfig.IMAGE_SIZE_SMALL_SQUARE[0]
					,FileConfig.IMAGE_SIZE_SMALL_SQUARE[1]);

		case BOTH_FROM_ROW:
			return new Point(FileConfig.IMAGE_SIZE_SMALL_ROW_GREATER_COL[0]
					,FileConfig.IMAGE_SIZE_SMALL_ROW_GREATER_COL[1]);

		case BOTH_FROM_COL:
			return new Point(FileConfig.IMAGE_SIZE_SMALL_ROW_LESS_COL[0]
					,FileConfig.IMAGE_SIZE_SMALL_ROW_LESS_COL[1]);

		case BOTH_BY_SQUARE:
			return new Point(FileConfig.IMAGE_SIZE_SMALL_SQUARE[0]
					,FileConfig.IMAGE_SIZE_SMALL_SQUARE[1]);

		default:
			return point;
		}
	}

	public static Point takeResizePointByMethodForResizeMedium (MethodForResize methodForResize
			, Point point) {
		switch (methodForResize) {
		case MINIMIZE_FROM_ROW:
			return new Point(FileConfig.IMAGE_SIZE_MEDIUM_ROW_GREATER_COL[0]
					,FileConfig.IMAGE_SIZE_MEDIUM_ROW_GREATER_COL[1]);

		case MINIMIZE_FROM_COL:
			return new Point(FileConfig.IMAGE_SIZE_MEDIUM_ROW_LESS_COL[0]
					,FileConfig.IMAGE_SIZE_MEDIUM_ROW_LESS_COL[1]);
			
		case MINIMIZE_BY_SQUARE:
			return new Point(FileConfig.IMAGE_SIZE_MEDIUM_SQUARE[0]
					,FileConfig.IMAGE_SIZE_MEDIUM_SQUARE[1]);

		case ENLARGE_FROM_ROW:
			return new Point(FileConfig.IMAGE_SIZE_MEDIUM_ROW_GREATER_COL[0]
					,FileConfig.IMAGE_SIZE_MEDIUM_ROW_GREATER_COL[1]);
			
		case ENLARGE_FROM_COL:
			return new Point(FileConfig.IMAGE_SIZE_MEDIUM_ROW_LESS_COL[0]
					,FileConfig.IMAGE_SIZE_MEDIUM_ROW_LESS_COL[1]);

		case ENLARGE_BY_SQUARE:
			return new Point(FileConfig.IMAGE_SIZE_MEDIUM_SQUARE[0]
					,FileConfig.IMAGE_SIZE_MEDIUM_SQUARE[1]);

		case BOTH_FROM_ROW:
			return new Point(FileConfig.IMAGE_SIZE_MEDIUM_ROW_GREATER_COL[0]
					,FileConfig.IMAGE_SIZE_MEDIUM_ROW_GREATER_COL[1]);

		case BOTH_FROM_COL:
			return new Point(FileConfig.IMAGE_SIZE_MEDIUM_ROW_LESS_COL[0]
					,FileConfig.IMAGE_SIZE_MEDIUM_ROW_LESS_COL[1]);

		case BOTH_BY_SQUARE:
			return new Point(FileConfig.IMAGE_SIZE_MEDIUM_SQUARE[0]
					,FileConfig.IMAGE_SIZE_MEDIUM_SQUARE[1]);

		default:
			return point;
		}
	}

	public static Point takeResizePointByMethodForResizeLarge (MethodForResize methodForResize
			, Point point) {
		switch (methodForResize) {
		case MINIMIZE_FROM_ROW:
			return new Point(FileConfig.IMAGE_SIZE_LARGE_ROW_GREATER_COL[0]
					,FileConfig.IMAGE_SIZE_LARGE_ROW_GREATER_COL[1]);

		case MINIMIZE_FROM_COL:
			return new Point(FileConfig.IMAGE_SIZE_LARGE_ROW_LESS_COL[0]
					,FileConfig.IMAGE_SIZE_LARGE_ROW_LESS_COL[1]);
			
		case MINIMIZE_BY_SQUARE:
			return new Point(FileConfig.IMAGE_SIZE_LARGE_SQUARE[0]
					,FileConfig.IMAGE_SIZE_LARGE_SQUARE[1]);

		case ENLARGE_FROM_ROW:
			return new Point(FileConfig.IMAGE_SIZE_LARGE_ROW_GREATER_COL[0]
					,FileConfig.IMAGE_SIZE_LARGE_ROW_GREATER_COL[1]);
			
		case ENLARGE_FROM_COL:
			return new Point(FileConfig.IMAGE_SIZE_LARGE_ROW_LESS_COL[0]
					,FileConfig.IMAGE_SIZE_LARGE_ROW_LESS_COL[1]);

		case ENLARGE_BY_SQUARE:
			return new Point(FileConfig.IMAGE_SIZE_LARGE_SQUARE[0]
					,FileConfig.IMAGE_SIZE_LARGE_SQUARE[1]);

		case BOTH_FROM_ROW:
			return new Point(FileConfig.IMAGE_SIZE_LARGE_ROW_GREATER_COL[0]
					,FileConfig.IMAGE_SIZE_LARGE_ROW_GREATER_COL[1]);

		case BOTH_FROM_COL:
			return new Point(FileConfig.IMAGE_SIZE_LARGE_ROW_LESS_COL[0]
					,FileConfig.IMAGE_SIZE_LARGE_ROW_LESS_COL[1]);

		case BOTH_BY_SQUARE:
			return new Point(FileConfig.IMAGE_SIZE_LARGE_SQUARE[0]
					,FileConfig.IMAGE_SIZE_LARGE_SQUARE[1]);

		default:
			return point;
		}
	}

	public static Point takeResizePointByMethodForResizeExtraLarge (MethodForResize methodForResize
			, Point point) {
		switch (methodForResize) {
		case MINIMIZE_FROM_ROW:
			return new Point(FileConfig.IMAGE_SIZE_EXTRA_LARGE_ROW_GREATER_COL[0]
					,FileConfig.IMAGE_SIZE_EXTRA_LARGE_ROW_GREATER_COL[1]);

		case MINIMIZE_FROM_COL:
			return new Point(FileConfig.IMAGE_SIZE_EXTRA_LARGE_ROW_LESS_COL[0]
					,FileConfig.IMAGE_SIZE_EXTRA_LARGE_ROW_LESS_COL[1]);
			
		case MINIMIZE_BY_SQUARE:
			return new Point(FileConfig.IMAGE_SIZE_EXTRA_LARGE_SQUARE[0]
					,FileConfig.IMAGE_SIZE_EXTRA_LARGE_SQUARE[1]);

		case ENLARGE_FROM_ROW:
			return new Point(FileConfig.IMAGE_SIZE_EXTRA_LARGE_ROW_GREATER_COL[0]
					,FileConfig.IMAGE_SIZE_EXTRA_LARGE_ROW_GREATER_COL[1]);
			
		case ENLARGE_FROM_COL:
			return new Point(FileConfig.IMAGE_SIZE_EXTRA_LARGE_ROW_LESS_COL[0]
					,FileConfig.IMAGE_SIZE_EXTRA_LARGE_ROW_LESS_COL[1]);

		case ENLARGE_BY_SQUARE:
			return new Point(FileConfig.IMAGE_SIZE_EXTRA_LARGE_SQUARE[0]
					,FileConfig.IMAGE_SIZE_EXTRA_LARGE_SQUARE[1]);

		case BOTH_FROM_ROW:
			return new Point(FileConfig.IMAGE_SIZE_EXTRA_LARGE_ROW_GREATER_COL[0]
					,FileConfig.IMAGE_SIZE_EXTRA_LARGE_ROW_GREATER_COL[1]);

		case BOTH_FROM_COL:
			return new Point(FileConfig.IMAGE_SIZE_EXTRA_LARGE_ROW_LESS_COL[0]
					,FileConfig.IMAGE_SIZE_EXTRA_LARGE_ROW_LESS_COL[1]);

		case BOTH_BY_SQUARE:
			return new Point(FileConfig.IMAGE_SIZE_EXTRA_LARGE_SQUARE[0]
					,FileConfig.IMAGE_SIZE_EXTRA_LARGE_SQUARE[1]);

		default:
			return point;
		}
	}
	
	public static Mat executeResize(int row, int col, Mat beforeMat) {
		Mat resizeImageMat = new Mat();
		Size size = new Size(row, col);
		resize(beforeMat, resizeImageMat, size);		
		return resizeImageMat;
	}
	
	public static String fileNameForRisizeImageFile (File tmpFile
			, String destinationUpload
			, String sizeType) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS");
		
		StringBuffer sb = new StringBuffer();
		sb.append(destinationUpload)
		  .append("/")
		  .append(df.format(LocalDateTime.now()))
		  .append("_")
		  .append(sizeType)
		  .append("_")
		  .append(tmpFile.getName());
		
		return sb.toString();
	}
	

	@SuppressWarnings("resource")
	public static File takeResizeImageData(String destinationUpload
			, String fileName
			, File tmpFile
			, String imageType
			, String contentType) {

        // 空ファイルの場合
        if (Objects.isNull(tmpFile)) {
        	return null;
        }

        // 画像じゃない場合
        if (Objects.isNull(contentType) 
        		|| !contentType.contains("image")) {
        	return null;
        }

		Mat mat = null;
		Mat resizeMat = new Mat();
//		byte[] bytes = null;
//		InputStream inputStream = null;
		File file = null;
		try {
			// MultipartFileからMatへ変換
			mat = readImage(tmpFile);

			// 画像の横幅と縦幅を取得する
			Point point = takeImageSize(mat);

			// 拡大・縮小の判定
			MethodForResize methodForResize = selectMethodForResizeImage(point);

			// リサイズの横幅と縦幅を取得する
			Point resizePoint = takeResizePoint(imageType, methodForResize, point);

			// リサイズ後のデータを取得する
			resizeMat = executeResize(resizePoint.x, resizePoint.y, mat);
			String tmpFileName = Paths.get(FileConfig.IMAGE_TMP_DIR, fileName).toString();
			imwrite(tmpFileName, resizeMat);
			file = new File(tmpFileName);
			//			ByteBuffer matOfByte = resizeMat.cvSize().asByteBuffer();
//			final String extention = "."+fileExtention;	
//			imencode(extention, resizeMat, matOfByte);
//			bytes = matOfByte.array();
//			bytes = resizeMat.asByteBuffer().array();
//			bytes = new byte[(int) (resizeMat.total() * resizeMat.channels())];
//			bytes = ((DataBufferByte) Java2DFrameUtils.toBufferedImage(resizeMat).getRaster().getDataBuffer()).getData();
			//			resizeMat.data().get(bytes);
			
//			Files.write(Paths.get(file.getAbsolutePath()) , bytes);
			//inputStream = new ByteArrayInputStream(bytes);
			
		} catch (Exception e) {

			log.error(e);

		} finally {
			resizeMat.close();
			mat.close();
		}
		return file;
	}

	public static boolean deleteTmpFile (String directoryPath) {
		File directory = new File(directoryPath);	
		File[] tmpFiles = Objects.requireNonNull(directory.listFiles());
        Arrays.stream(tmpFiles)
          .filter(Predicate.not(File::isDirectory))
          .forEach(File::delete);
		return true;
	}
}
