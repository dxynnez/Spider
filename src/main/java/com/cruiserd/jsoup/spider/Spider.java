package com.cruiserd.jsoup.spider;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface Spider {

    /**
     * 
     * @param username
     * @param password
     * @return mapping of all cookies
     * @throws IOException
     */
	Map<String, String> login(String username, String password) throws Exception;
	
	boolean reply(String postId, Map<String, String> contents, Map<String, String> cookies) throws Exception;
	
	/**
	 * 
	 * @param cookies retrieved from {@link #login(String, String) login} method
	 * @return A set of new posts' id
	 * @throws Exception
	 */
	Set<String> getPostIds(Map<String, String> cookies) throws Exception;
}
