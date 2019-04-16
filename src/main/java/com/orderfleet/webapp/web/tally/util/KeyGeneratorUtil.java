package com.orderfleet.webapp.web.tally.util;

import java.util.Random;

/**
 * a Key-Generator.
 *
 * @author Sarath
 * @since Oct 29, 2016
 */
public class KeyGeneratorUtil {

	private static final char[] alphabet = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
	private static final Random rand = new Random();

	public static String getRandomAlphaNumericString(final int length) {
		if (length < 0) {
			throw new IllegalArgumentException("Invalid length to create random string");
		}
		final StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int randomIndex = rand.nextInt(alphabet.length);
			sb.append(alphabet[randomIndex]);
		}
		return sb.toString();
	}

	public static String getRandomCustomString(final char[] str, final int length) {
		if (length < 0) {
			throw new IllegalArgumentException("Invalid length to create random string");
		}
		final StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int randomIndex = rand.nextInt(str.length);
			sb.append(str[randomIndex]);
		}
		return sb.toString();
	}
}