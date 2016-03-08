package com.cruiserd.jsoup.spider;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.cruiserd.jsoup.exception.LoginException;
import com.cruiserd.jsoup.util.Encoder;
import com.cruiserd.jsoup.util.SinaLoginJSONModel;
import com.cruiserd.jsoup.util.URLParser;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

public class WeiboSpider implements Spider {
    
    /*
     * main function only for testing purpose.
     */
//    public static void main(String[] args) throws Exception {
//        Spider spider = new WeiboSpider();
//        Map<String, String> cookies = spider.login("YOUR USERNAME", "YOURPASSWORD");
//        for(String post : spider.getPostIds(cookies)) {
//            System.out.println(post);
//        }
//    }
    
    @Override
    public Map<String, String> login(String username, String password) throws Exception {
        
        Map<String, String> map = ImmutableMap.<String, String> builder()
                .put("entry", "sso")
                .put("gateway", "1")
                .put("from", "null")
                .put("savestate", "30")
                .put("useticket", "0")
                .put("pagerefer", "")
                .put("vsnf", "1")
                .put("su", Encoder.base64Encode(username))
                .put("service", "sso")
                .put("sp", password)
                .put("sr", "1440*900")
                .put("encoding", "UTF-8")
                .put("cdult", "3")
                .put("domain", "sina.com.cn")
                .put("prelt", "0")
                .put("returntype", "TEXT")
                .build();
        
        String json = Jsoup.connect("https://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.15)")
                           .data(map)
                           .method(Method.POST)
                           .execute()
                           .body();
        
        Gson gson = new Gson();
        String url = gson.fromJson(json, SinaLoginJSONModel.class).getCrossDomainUrlList().get(0);
        Map<String, String> params = URLParser.extractParametersFromURL(url);
        if(!params.containsKey("ticket") || !params.containsKey("ssosavestate")) {
            throw new LoginException("Cannot retrieve required parameters, login was terminated.");
        }
        String ticket = params.get("ticket");
        String ssosavestate = params.get("ssosavestate");
        String loginURL = "https://passport.weibo.com/wbsso/login?ticket=" + 
                          ticket + "&ssosavestate=" + ssosavestate +
                          "&callback=sinaSSOController.doCrossDomainCallBack&scriptId=ssoscript0&client=ssologin.js(v1.4.15)";
        Map<String, String> cookies = Jsoup.connect(loginURL)
                                           .ignoreContentType(true)
                                           .method(Method.GET)
                                           .execute()
                                           .cookies();
        return cookies;
    }
    
    // TODO 
    @Override
    public boolean reply(String postID, Map<String, String> contents, Map<String, String> cookies) throws Exception {
        return false;
    }
    
    @Override
    public Set<String> getPostIds(Map<String, String> cookies) throws Exception {
        String postDest = "http://weibo.com";
        Document doc = Jsoup.connect(postDest)
                            .cookies(cookies)
                            .get();
        
        // if return set is empty, most likely the index of target script has changed 
        String ele = doc.getElementsByTag("script").get(14).toString();
        String regEx = "(?s)(?<=mid=\\\\\")\\d+?(?=\\\\\".+action)";
        Set<String> result = new HashSet<> ();
        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(ele);
        while(mat.find()) {
            result.add(mat.group());
        }
        return result;
    }
}