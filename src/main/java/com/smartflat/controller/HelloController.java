package com.smartflat.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class HelloController {

    public static String COOKIE_TOKEN = "csrftoken";

    @RequestMapping(method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {
        model.addAttribute("message", "Spring 3 MVC Hello World");
        test();
        return "hello";

    }

    private void test() {
        System.out.println("Start test......");

        CookieStore cookieStore = new BasicCookieStore();
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("BTN_LOGIN", "Test"));
        urlParameters.add(new BasicNameValuePair("LOGIN", "LS01406548"));
        urlParameters.add(new BasicNameValuePair("PSWD", ""));

        String htmlResult = null;
        try {
            htmlResult = postRequest(urlParameters, localContext);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String userId = getUserId(htmlResult);
        System.out.println("userId=" + userId);
        String srchhouse = getAttribute(htmlResult, "srchhouse");
        System.out.println("srchhouse=" + srchhouse);
        String srchls = getAttribute(htmlResult, "srchls");
        System.out.println("srchls=" + srchls);
        List<String> srchpu = getList(htmlResult, "srchpu");
        System.out.println("srchpu=" + srchpu);
        String srchrgn = getAttribute(htmlResult, "srchrgn");
        System.out.println("srchrgn=" + srchrgn);
        String srchstr = getAttribute(htmlResult, "srchstr");
        System.out.println("srchstr=" + srchstr);

        List<NameValuePair> addedParameters = new ArrayList<NameValuePair>();
        addedParameters.add(new BasicNameValuePair("ADD_CDDATE", "08/02/2014"));
        addedParameters.add(new BasicNameValuePair("ADD_CDERR", ""));
        addedParameters.add(new BasicNameValuePair("ADD_CDNOTE", "Mistake"));
        addedParameters.add(new BasicNameValuePair("ADD_CDV1", "0.2420"));
        addedParameters.add(new BasicNameValuePair("BTN_CDADD.x", "5"));
        addedParameters.add(new BasicNameValuePair("BTN_CDADD.y", "5"));

        addedParameters.add(new BasicNameValuePair("page", "11"));
        addedParameters.add(new BasicNameValuePair("srchhouse", "342"));
        addedParameters.add(new BasicNameValuePair("srchls", "202"));
        addedParameters.add(new BasicNameValuePair("srchotv	", "-1"));
        addedParameters.add(new BasicNameValuePair("srchpu", "2317"));
        addedParameters.add(new BasicNameValuePair("srchrgn", "1"));
        addedParameters.add(new BasicNameValuePair("srchstr", "6"));
        addedParameters.add(new BasicNameValuePair("testid", "922"));
        addedParameters.add(new BasicNameValuePair("userid", "922"));
        try {
            htmlResult = postRequest(urlParameters, localContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("End test");
    }

    private String postRequest(List<NameValuePair> params, HttpContext context)
            throws ClientProtocolException, IOException {
        String url = "http://teplo.dn.ua:8383/FLPU/flpu";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost requestPost = new HttpPost(url);

        requestPost.addHeader("User-Agent", "Mozilla/5.0");
        requestPost.addHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        requestPost.addHeader("Accept-Encoding", "gzip, deflate");
        requestPost.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = null;

        response = client.execute(requestPost, context);
        System.out.println("Status: " + response.getStatusLine().getStatusCode());
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity()
                .getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        System.out.println(result);
        System.out.println("________________");
        CookieStore store = (CookieStore) context.getAttribute(HttpClientContext.COOKIE_STORE);
        List<Cookie> cookies = store.getCookies();
        System.out.println("COOKIE: " + cookies.toString());
        return result.toString();
    }

    public String getUserId(String html) {
        Document doc = Jsoup.parse(html);
        Element related = doc.getElementById("bodypage");
        if (related != null) {
            Elements items = related.getElementsByAttributeValue("name", "userid");
            if (items.size() > 0) {
                return items.get(0).attr("value");
            }
        }
        return "";
    }

    public String getAttribute(String html, String attrName) {
        Document doc = Jsoup.parse(html);
        Element related = doc.getElementById("dsearch");
        if (related != null) {
            Elements items = related.getElementsByAttributeValue("name", attrName);
            if (items.size() > 0) {
                return items.get(0).attr("value");
            }
        }
        return "";
    }

    public List<String> getList(String html, String attrName) {
        List<String> result = new ArrayList<String>();
        Document doc = Jsoup.parse(html);
        Element related = doc.getElementById("dsearch");
        if (related != null) {
            Elements items = related.getElementsByAttributeValue("name", attrName);
            if (items.size() > 0) {
                Elements children = items.get(0).children();
                for (Element child : children) {
                    System.out.println(child.ownText() + "=" + child.attr("value"));
                    result.add(child.attr("value"));
                }
            }
        }
        return result;
    }

}
