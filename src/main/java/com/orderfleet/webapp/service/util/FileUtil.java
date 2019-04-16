package com.orderfleet.webapp.service.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

/**
 * Utility class for file.
 * 
 * @author Shaheer
 * @since August 01, 2016
 */
public class FileUtil {
	private MessageDigest messageDigest;
	private static FileUtil instance;

	private FileUtil() {
		try {
			this.messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}

	public static FileUtil getInstance() {
		if (FileUtil.instance == null) {
			FileUtil.instance = new FileUtil();
		}
		return FileUtil.instance;
	}

	public static String getMd5(final byte[] fileBytes) throws NoSuchAlgorithmException {
		return DatatypeConverter.printHexBinary(FileUtil.getInstance().messageDigest.digest(fileBytes));
	}

}
