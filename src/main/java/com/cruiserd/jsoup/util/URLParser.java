package com.cruiserd.jsoup.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class URLParser {
	public static Map<String, String> extractParametersFromURL(String url) throws MalformedURLException {
		URL aURL = new URL(url);
		ImmutableMap.Builder<String, String> ib = new ImmutableMap.Builder<String, String>();
		String[] params = aURL.getQuery().split("&");
		for(String param : params) {
			String[] pair = param.split("=");
			ib.put(pair[0], pair[1]);
		}
		
		return ib.build();
	}
}
