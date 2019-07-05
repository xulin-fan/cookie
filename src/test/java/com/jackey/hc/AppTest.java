package com.jackey.hc;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * 声明httpClient为成员变量, 该案例集中所有请求使用同一个httpClient, 该条件为cookie托管的前提
     */
    private final static HttpClient httpClient;
    private final static String domain = "http://localhost:8282";
    private final static String loginUrl = domain.concat("/login");
    private final static String indexUrl = domain.concat("/index.jsp");

    static {
        // 设置链接与读取的超时时间
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(60000).
                setConnectTimeout(60000).build();

        // 创建httpClient
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig)
                // 如果使用托管cookie的功能,那么在httpclient请求后, 会解析responses的头部信息,
                // 并将Set-Cookie头部内容拿出存储在cookieStore中, 则以后如果在使用此httpClient
                // 发送请求时会自动上送cookie, 不需要手动设置
                .setDefaultCookieStore(new BasicCookieStore()).build();
    }


    /**
     * 执行其它案例前需先登录
     *
     * @throws IOException
     */
    @Before
    public void init() throws IOException {
        login();
    }

    /**
     * 请求首页案例
     *
     * @throws IOException
     */
    @Test
    public void testIndex() throws IOException {
        String response = index();
        System.out.println("响应信息: " + response);

        // 如果页面中不包含用户名信息，证明该案例通过测试
        Assert.assertTrue(!response.contains("用户名"));
    }

    /**
     * 登录方法
     *
     * @throws IOException
     */
    private void login() throws IOException {

        // 声明post方法及设置登录地址
        HttpPost post = new HttpPost(loginUrl);

        // 规避login_service程序的一个bug, 忽略即可
        post.setHeader("cookie", "xxxx=xxxx");

        // 声明参数
        List<NameValuePair> parameters = new ArrayList();
        NameValuePair username = new BasicNameValuePair("username", "jjj");
        NameValuePair password = new BasicNameValuePair("password", "123456");
        NameValuePair remeber = new BasicNameValuePair("remeber", "remeber");
        parameters.add(username);
        parameters.add(password);
        parameters.add(remeber);

        // 设置表单
        post.setEntity(new UrlEncodedFormEntity(parameters));

        // 提交表单
        httpClient.execute(post);
    }

    /**
     * 请求首页测试用例
     *
     * @return
     * @throws IOException
     */
    private String index() throws IOException {

        // 声明get方法及设置首页地址
        HttpGet get = new HttpGet(indexUrl);

        // 规避login_service程序的一个bug, 忽略即可
        get.setHeader("cookie", "xxxx=xxxx");

        // 提交表单并返回响应内容
        return EntityUtils.toString(httpClient.execute(get).getEntity());
    }
}
