package com.vxplo.vxshow.util;

import java.io.File;
import java.io.FileDescriptor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Environment;

public class FileUtil {
	public static File updateFile;
	
	public static boolean hasSdcard() {
		return (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()));
	}
	
	public static File getUpdateFile(Context context) {
		File file = new File(context.getFilesDir(), "update.apk");
		return file;
	}
	
	/**
	 * 删除文件或文件夹
	 * @param path
	 */
	public static void deleteFile(String path) {
		File file = new File(path);
		try {
			if(file.exists()) {
				if(!file.isDirectory()) {
					file.delete();
				} else if(file.isDirectory()) {
					String[] dirs = file.list();
					if(dirs != null) {
						for(String p : dirs) {
							deleteFile(p);
						}
					}
					file.delete();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 清空文件夹
	 * @param path
	 */
	public static void cleanDirecory(String path) {
		File file = new File(path);
		try {
			if(file.exists() && file.isDirectory()) {
				File[] dirs = file.listFiles();
				if(dirs != null) {
					for(File f : dirs) {
						String p = f.getAbsolutePath();
						deleteFile(p);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getVxShowFileDir() {
		String fileDir;
		if(hasSdcard()) {
			fileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/vxshow";
			File file = new File(fileDir);
			if(!file.exists()) {
				file.mkdir();
			}
			
			return fileDir;
		}else {
			return null;
		}
	}
	
	public static String getVxRecordsDir() {
		if(hasSdcard()) {
			String fileDir = getVxShowFileDir() + "/records";
			File file = new File(fileDir);
			if(!file.exists()) {
				file.mkdirs();
			}
			return fileDir;
		}else {
			return null;
		}
	}
	
	public static String getVxCacheDir() {
		if(hasSdcard()) {
			String fileDir = getVxShowFileDir() + "/cache";
			File file = new File(fileDir);
			if(!file.exists()) {
				file.mkdirs();
			}
			return fileDir;
		}else {
			return null;
		}
	}
	
	public static String getVxVideosDir() {
		if(hasSdcard()) {
			String fileDir = getVxShowFileDir() + "/videos";
			File file = new File(fileDir);
			if(!file.exists()) {
				file.mkdirs();
			}
			return fileDir;
		}else {
			return null;
		}
	}
	
	public static String getVxImagesDir() {
		if(hasSdcard()) {
			String fileDir = getVxShowFileDir() + "/images";
			File file = new File(fileDir);
			if(!file.exists()) {
				file.mkdirs();
			}
			return fileDir;
		}else {
			return null;
		}
	}
	
	public static List<String> listRecords() {
		String dir = getVxRecordsDir();
		File dirFile = new File(dir);
		File[] records = dirFile.listFiles();
		List<String> recordNames = new ArrayList<String>();
		if(records != null) {
			for(File file : records) {
				recordNames.add(file.getAbsolutePath());
			}
		}
		return recordNames;
	}
	
	/**
	 * 生成一个录像的存储路径
	 * @return
	 */
	public static String getOneVxVideoPath() {
		String dir = getVxVideosDir();
		if(dir == null) {
			return null;
		}
		Date date = new Date();
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String str = formater.format(date);
		String path = dir + File.separator + "video" + str + ".mp4";
		return path;
	}

}
