package com.orderfleet.webapp.web.tally.util;

/**
 * used for replace all special characters
 *
 * @author Sarath
 * @since Oct 29, 2016
 */
public class StringUtilsCustom {

	public static String replaceSpecialCharacters(String value) {
		value = value.replaceAll("amp;", "&");
		value = value.replaceAll("apos;", "'");
		value = value.replaceAll("quot;", "\"");
		return value;
	}

	public static String replaceSpecialCharactersWithXmlValue(String value) {
		value = value.replaceAll("&", "&amp;");
		value = value.replaceAll("'", "&apos;");
		value = value.replaceAll("\"", "&quot;");
		return value;
	}

}
