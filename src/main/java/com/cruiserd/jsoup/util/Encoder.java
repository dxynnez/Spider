package com.cruiserd.jsoup.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Encoder {
	public static String base64Encode(String input) throws UnsupportedEncodingException {
    	byte[] inputByte = input.getBytes("UTF-8");
    	return Base64.getEncoder().encodeToString(inputByte);
	}
}
