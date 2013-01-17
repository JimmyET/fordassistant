package com.zzgo.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

import android.content.Context;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Video;
import android.text.TextUtils;

public class FileUtils {
	/**
	 * KB
	 */
	public static final long ONE_KB = 1024;

	/**
	 * MB
	 */
	public static final long ONE_MB = ONE_KB * ONE_KB;

	/**
	 * GB
	 */
	public static final long ONE_GB = ONE_KB * ONE_MB;

	private static final int BUF_SIZE = 1024;
	private static final String TAG = "FileUtils";

	public static String writeToFile(Context context, InputStream is, String filename) {
		if (is == null || context == null || TextUtils.isEmpty(filename))
			return null;
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		touchFile(context, filename);
		LogUtil.d(TAG, "writeToFile : " + filename);
		try {
			in = new BufferedInputStream(is);
			out = new BufferedOutputStream(new FileOutputStream(getFile(context, filename)));
			byte[] buffer = new byte[BUF_SIZE];
			int l;
			while ((l = in.read(buffer)) != -1) {
				// LogUtil.d("writeToFile", "length : " + l);
				out.write(buffer, 0, l);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
				is.close();
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return context.getFilesDir() + File.separator + filename;
	}

	public static String writeToFile(Context context, byte[] bytes, String filename) {
		return writeToFile(context, bytes, filename, false);
	}

	public static String writeToFile(Context context, byte[] bytes, String filename, boolean isAppend) {
		if (bytes == null || context == null || TextUtils.isEmpty(filename))
			return null;
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(getFile(context, filename), isAppend));
			out.write(bytes);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			LogUtil.e(TAG, "IOException : " + ioe.getMessage());
		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return context.getFilesDir() + File.separator + filename;
	}

	/**
	 * write a string To a File
	 * 
	 * @param context
	 * @param file
	 * @param string
	 * @param isAppend
	 * @return
	 */
	public static boolean writeStringToFile(File file, String string, boolean isAppend) {
		boolean isWriteOk = false;

		if (null == file || null == string) {
			return isWriteOk;
		}

		FileWriter fw = null;
		try {
			fw = new FileWriter(file, isAppend);

			fw.write(string, 0, string.length());
			fw.flush();
			isWriteOk = true;
		} catch (Exception e) {
			isWriteOk = false;
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					isWriteOk = false;
					e.printStackTrace();
				}
			}
		}
		return isWriteOk;
	}

	/**
	 * 根据文件URI判断是否为媒体文件
	 * 
	 * @param uri
	 * @return
	 */
	public static boolean isMediaUri(String uri) {
		if (uri.startsWith(Audio.Media.INTERNAL_CONTENT_URI.toString())
				|| uri.startsWith(Audio.Media.EXTERNAL_CONTENT_URI.toString())
				|| uri.startsWith(Video.Media.INTERNAL_CONTENT_URI.toString())
				|| uri.startsWith(Video.Media.EXTERNAL_CONTENT_URI.toString())) {
			return true;
		} else {
			return false;
		}
	}

	public static void copyfile(File src, File dec) {
		try {
			if (src == null || dec == null) {
				return;
			}

			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dec);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void clearFile(Context context, String filename) {
		if (context == null || TextUtils.isEmpty(filename))
			return;
		File file = getFile(context, filename);
		LogUtil.d(TAG, "clearFile path : " + file.getAbsolutePath());
		File dir = file.getParentFile();
		if (!dir.exists()) {
			LogUtil.d(TAG, "dir not exists");
			dir.mkdirs();
		}
		if (file.exists())
			file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void touchFile(Context context, String filename) {
		if (context == null || TextUtils.isEmpty(filename))
			return;
		File file = getFile(context, filename);
		LogUtil.d(TAG, "touchFile path : " + file.getAbsolutePath());
		File dir = file.getParentFile();
		if (!dir.exists()) {
			LogUtil.d(TAG, "dir not exists");
			dir.mkdirs();
		}
		if (file.exists())
			return;
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File getFile(Context context, String filename) {
		if (context == null || TextUtils.isEmpty(filename))
			return null;
		return new File(context.getFilesDir().getAbsoluteFile() + filename);

	}

	/**
	 * 根据文件URI得到文件扩展名
	 * 
	 * @param uri
	 * @return
	 */
	public static String getExtension(String uri) {
		if (uri == null) {
			return null;
		}

		int dot = uri.lastIndexOf(".");
		if (dot >= 0) {
			return uri.substring(dot);
		} else {
			// No extension.
			return "";
		}
	}

	/**
	 * 判断是否为本地文件
	 * 
	 * @param uri
	 * @return
	 */
	public static boolean isLocal(String uri) {
		if (uri != null && !uri.startsWith("http://")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断文件是否为视频文件
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean isVideo(String filename) {
		String mimeType = getMimeType(filename);
		if (mimeType != null && mimeType.startsWith("video/")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断文件是否为音频文件
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean isAudio(String filename) {
		String mimeType = getMimeType(filename);
		if (mimeType != null && mimeType.startsWith("audio/")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据文件名得到文件的mimetype 简单判断,考虑改为xml文件配置关联
	 * 
	 * @param filename
	 * @return
	 */
	public static String getMimeType(String filename) {
		String mimeType = null;

		if (filename == null) {
			return mimeType;
		}
		if (filename.endsWith(".3gp")) {
			mimeType = "video/3gpp";
		} else if (filename.endsWith(".mid")) {
			mimeType = "audio/mid";
		} else if (filename.endsWith(".mp3")) {
			mimeType = "audio/mpeg";
		} else if (filename.endsWith(".xml")) {
			mimeType = "text/xml";
		} else {
			mimeType = "";
		}
		return mimeType;
	}

	/**
	 * 将文件大小的long值转换为可读的文字
	 * 
	 * @param size
	 * @return 10KB或10MB或1GB
	 */
	public static String byteCountToDisplaySize(long size) {
		String displaySize;

		if (size / ONE_GB > 0) {
			displaySize = String.valueOf(size / ONE_GB) + " GB";
		} else if (size / ONE_MB > 0) {
			displaySize = String.valueOf(size / ONE_MB) + " MB";
		} else if (size / ONE_KB > 0) {
			displaySize = String.valueOf(size / ONE_KB) + " KB";
		} else {
			displaySize = String.valueOf(size) + " bytes";
		}
		return displaySize;
	}

	public static boolean isDirectory(File file) {
		return file.exists() && file.isDirectory();
	}

	public static boolean isFile(File file) {
		return file.exists() && file.isFile();
	}

	public static boolean createNewDirectory(File file) {
		if (file.exists() && file.isDirectory()) {
			return false;
		}
		return file.mkdirs();
	}

	public static void delDirectory(String directoryPath) {
		try {
			delAllFile(directoryPath); // 删除完里面所有内容
			String filePath = directoryPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delDirectory(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	public static boolean delAllFileWithoutDir(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFileWithoutDir(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				flag = true;
			}
		}
		return flag;
	}

	// -------------------- 获得文件的md5等hash值---------------------//
	public final static String HASH_TYPE_MD5 = "MD5";
	public final static String HASH_TYPE_SHA1 = "SHA1";
	public final static String HASH_TYPE_SHA1_256 = "SHA-256";
	public final static String HASH_TYPE_SHA1_384 = "SHA-384";
	public final static String HASH_TYPE_SHA1_512 = "SHA-512";
	public static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String getHash(String fileName, String hashType) throws Exception {
		InputStream fis;
		fis = new FileInputStream(fileName);
		byte[] buffer = new byte[1024];
		MessageDigest md5 = MessageDigest.getInstance(hashType);
		int numRead = 0;
		while ((numRead = fis.read(buffer)) > 0) {
			md5.update(buffer, 0, numRead);
		}
		fis.close();
		return toHexString(md5.digest());
	}

	public static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			sb.append(hexChar[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	// 处理lossless音频格式的文件。
	// zhouhenlei add .2010-10-14
	// 音频格式
	public static final String[] AUDIO_EXTS = { ".flac", ".FLAC", ".ape", ".APE", ".wv", ".WV", ".mpc", ".MPC", "m4a",
			"M4A", ".wav", ".WAV", ".mp3", ".MP3", ".wma", ".WMA", ".ogg", ".OGG", ".3gpp", ".3GPP", ".aac", ".AAC" };
	// 无损音频格式
	public static final String[] LOSSLESS_EXTS = { ".flac", ".FLAC", ".ape", ".APE", ".wv", ".WV", ".mpc", ".MPC",
			"m4a", "M4A", ".wav", ".WAV" };
	// 有损音频格式
	public static final String[] LOSS_EXTS = { ".mp3", ".MP3", ".wma", ".WMA", ".ogg", ".OGG", ".3gpp", ".3GPP",
			".aac", ".AAC" };

	// 用在ape,flac的cue后缀
	public static final String CUE_EXT = ".cue";
	// 自定义的播放列表后缀，暂时不用。
	public static final String CUSTOM_PLAYLIST_EXT = ".playlist";
	// 列表格式
	public static final String[] PLAYLIST_EXTS = { CUSTOM_PLAYLIST_EXT, ".m3u", ".M3U", ".pls", ".PLS" };
	// 自定义书签到后缀
	public static final String BOOKMARK_EXT = ".bmark";

	// true :lossless
	// false:loss
	public static boolean isLosslessSupported(File f) {
		String s = f.toString();
		if (s.endsWith(".flac") || s.endsWith(".FLAC"))
			return true;
		else if (s.endsWith(".ape") || s.endsWith(".APE"))
			return true;
		else if (s.endsWith(".wav") || s.endsWith(".WAV"))
			return true;
		else if (s.endsWith(".wv") || s.endsWith(".WV"))
			return true;
		else if (s.endsWith(".mpc") || s.endsWith(".MPC"))
			return true;
		else if (s.endsWith(".m4a") || s.endsWith(".M4A"))
			return true;
		else
			return false;
	}

	public static boolean isLosslessSupported(String s) {
		// if ((s == null) || (s.length() == 0))
		// return false;
		// if (s.endsWith(".flac") || s.endsWith(".FLAC"))
		// return true;
		// else if (s.endsWith(".ape") || s.endsWith(".APE"))
		// return true;
		// else if (s.endsWith(".wav") || s.endsWith(".WAV"))
		// return true;
		// else if (s.endsWith(".wv") || s.endsWith(".WV"))
		// return true;
		// else if (s.endsWith(".mpc") || s.endsWith(".MPC"))
		// return true;
		// else if (s.endsWith(".m4a") || s.endsWith(".M4A"))
		// return true;
		// else
		return false;
	}

	public static String loadString(Context context, String filename) {
		if (context == null || TextUtils.isEmpty(filename))
			return null;
		File file = getFile(context, filename);
		return loadString(file);
	}

	/**
	 * read file to a string
	 * 
	 * @param context
	 * @param file
	 * @return
	 */
	public static String loadString(File file) {
		if (null == file || !file.exists()) {
			return "";
		}
		FileInputStream fis = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			fis = new FileInputStream(file);
			int restSize = fis.available();
			int bufSize = restSize > BUF_SIZE ? BUF_SIZE : restSize;
			byte[] buf = new byte[bufSize];
			while (fis.read(buf) != -1) {
				baos.write(buf);
				restSize -= bufSize;

				if (restSize <= 0)
					break;
				if (restSize < bufSize)
					bufSize = restSize;
				buf = new byte[bufSize];
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return baos.toString();
	}

	public static long getFolderSize(File folder) throws IllegalArgumentException {
		// Validate
		if (folder == null || !folder.isDirectory())
			throw new IllegalArgumentException("Invalid   folder ");
		String list[] = folder.list();
		if (list == null || list.length < 1)
			return 0;

		// Get size
		File object = null;
		long folderSize = 0;
		for (int i = 0; i < list.length; i++) {
			object = new File(folder, list[i]);
			if (object.isDirectory())
				folderSize += getFolderSize(object);
			else if (object.isFile())
				folderSize += object.length();
		}
		return folderSize;
	}
}
