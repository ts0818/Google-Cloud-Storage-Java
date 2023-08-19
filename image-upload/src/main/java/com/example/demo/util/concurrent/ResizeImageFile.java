package com.example.demo.util.concurrent;

import java.io.File;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;

import com.example.demo.util.FileUtil;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResizeImageFile implements Runnable {

	private File tmpFile;

	private String conentType;

//	private String fileExtention;

	/** アップロード先 */
	private String destinationUpload;

	private ConcurrentLinkedQueue<String> imageSizeTypeArr;
	
	private String currentImageType;

	/** リサイズ結果を格納用 */
	private ConcurrentHashMap<String, File> resizeImageFileDataByteMap;

	/** 同期 */
	private CyclicBarrier barrier;

	@Override
	public void run() {

		while (! imageSizeTypeArr.isEmpty()) {

			String imageSizeType = imageSizeTypeArr.peek();
			// リサイズ後のファイル名
			String fileName = FileUtil.fileNameForRisizeImageFile(this.tmpFile
					, this.destinationUpload
					, imageSizeType);

			// 画像リサイズ後のデータ
			File imageresizeFile = FileUtil.takeResizeImageData(this.destinationUpload
					, fileName
					, this.tmpFile
					, imageSizeType
					, this.conentType);

			imageSizeTypeArr.remove(imageSizeType);

			// 結果を格納
			this.resizeImageFileDataByteMap.putIfAbsent(fileName, imageresizeFile);
			try {
				// 同期
				barrier.await();

			} catch (InterruptedException | BrokenBarrierException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

	}
}
