package com.hotusm.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

/**
 * httpClient工具类<br>
 * 该工具类目前仅支持Http请求，Https请求请走其他工具类
 */
public class HttpUtil {

    private final static Logger log = LogManager.getLogger(HttpUtil.class);

    private static final int CONNECTION_TIMEOUT = ConfigUtil.getIntValue("http.connection.timeout");
    private static final int SOCKET_TIMEOUT = ConfigUtil.getIntValue("http.socket.timeout");
    private static final int REQUEST_TIMEOUT = ConfigUtil.getIntValue("http.request.timeout");

    private static final int MAX_CONNECTION = ConfigUtil.getIntValue("http.connection.maxnum");
    private static final int MAX_ROUTE_CONNECTIONS = ConfigUtil.getIntValue("http.route.maxnum");

    private static final RequestConfig defalutRequectCfg;
    private static final Object lock = new Object();

    private static HttpClientBuilder clientBuilder;

    static {
        // 默认配置，设默认的连接超时时间、请求超时时间、结果超时时间
        Builder reqCfg = RequestConfig.custom();
        reqCfg.setSocketTimeout(SOCKET_TIMEOUT);
        reqCfg.setConnectTimeout(CONNECTION_TIMEOUT);
        reqCfg.setConnectionRequestTimeout(REQUEST_TIMEOUT);
        defalutRequectCfg = reqCfg.build();
    }

    private static HttpClientBuilder getClientBuilder() {
        if (clientBuilder != null) {
            return clientBuilder;
        }

        synchronized (lock) {
            if (clientBuilder != null) {
                return clientBuilder;
            }
            // 实例化manager
            RegistryBuilder<ConnectionSocketFactory> registBuilder = RegistryBuilder.<ConnectionSocketFactory> create();
            registBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);
            PoolingHttpClientConnectionManager conMgr = new PoolingHttpClientConnectionManager(registBuilder.build());
            conMgr.setMaxTotal(MAX_CONNECTION);

            // Modify by qiesai
            // 默认没有设置，高并发下回报大量的 TimeOutException
            conMgr.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
            // End Modify

            // 设置配置
            HttpClientBuilder cBuilder = HttpClientBuilder.create();
            cBuilder.setConnectionManager(conMgr);
            cBuilder.setDefaultRequestConfig(defalutRequectCfg);

            clientBuilder = cBuilder;
        }
        return clientBuilder;
    }

    /**
     * 从pool中获取一个HttpClient对象来执行Get/Post/Delete/Put等方法
     *
     * @return
     * @throws Exception
     */
    private static HttpClient getClient() throws Exception {
        // 实例化客户端
        CloseableHttpClient client = getClientBuilder().build();
        return client;
    }

    /**
     * 发送Get请求，参数默认
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static HttpReturnMessage doGet(String url) throws Exception {
        return doGet(url, null, null, -1);
    }

    /**
     * 发送Get请求，带有超时时间<br>
     * 其他默认
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static HttpReturnMessage doGet(String url, int timeout) throws Exception {
        return doGet(url, null, null, timeout);
    }

    /**
     * 放Get请求，带有参数<br>
     * 其他默认
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static HttpReturnMessage doGet(String url, Map<String, String> params) throws Exception {
        return doGet(url, params, null, -1);
    }

    /**
     * 发用Get请求，带有超时时间和请求参数
     *
     * @param url
     * @param params
     * @param timeout
     * @return
     * @throws Exception
     */
    public static HttpReturnMessage doGet(String url, Map<String, String> params, int timeout) throws Exception {
        return doGet(url, params, null, timeout);
    }

    /**
     * 发送Get请求，自定义所有参数<br>
     *
     * @param httpUrl
     * @param params
     * @param headers
     * @param timeout
     * @return
     * @throws Exception
     */
    public static HttpReturnMessage doGet(String httpUrl, Map<String, String> params, Map<String, String> headers,
                                          int timeout) throws Exception {

        if (log.isDebugEnabled()) {
            int t = timeout;
            if (t < 0) {
                t = REQUEST_TIMEOUT;
            }
            String msg = String
                    .format("Inovke Http Interface By Get Method , The URL is %s , params  = [ %s ] , headers = [ %s ] , timeout = %d",
                            httpUrl, params, headers, t);
            log.debug(msg);
        }

        // 构建Url
        URIBuilder url = new URIBuilder(httpUrl);
        url.setCharset(Charset.forName("UTF-8"));

        // 设置请求参数
        if (params != null) {
            for (Entry<String, String> entry : params.entrySet()) {
                url.addParameter(entry.getKey(), entry.getValue());
            }
        }

        HttpGet httpGet = new HttpGet(url.build());
        // 设置特殊的超时时间
        if (timeout > 0) {
            Builder cfg = RequestConfig.copy(defalutRequectCfg);
            cfg.setConnectTimeout(timeout);
            httpGet.setConfig(cfg.build());
        }

        // 设置header
        if (headers != null) {
            for (Entry<String, String> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // 执行请求
        HttpResponse response = getClient().execute(httpGet);
        return getHttpBody(response);
    }

    /**
     * 发送post请求，使用默认超时时间
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static HttpReturnMessage doPost(String url, Map<String, String> params) throws Exception {
        return doPost(url, params, null, -1);
    }

    /**
     * 发送post请求，带有请求参数和超时时间
     *
     * @param url
     * @param params
     * @param timeout
     * @return
     * @throws Exception
     */
    public static HttpReturnMessage doPost(String url, Map<String, String> params, int timeout) throws Exception {
        return doPost(url, params, null, timeout);
    }

    /**
     * 发送post请求,全部参数自定义
     *
     * @param url
     * @param params
     * @param headers
     * @param timeout
     * @return
     * @throws Exception
     */
    public static HttpReturnMessage doPost(String url, Map<String, String> params, Map<String, String> headers,
                                           int timeout) throws Exception {

        if (log.isDebugEnabled()) {
            int t = timeout;
            if (t < 0) {
                t = REQUEST_TIMEOUT;
            }
            String msg = String
                    .format("Inovke Http Interface By Post Method , The URL is %s , params  = [ %s ] , headers = [ %s ] , timeout = %d",
                            url, params, headers, t);
            log.debug(msg);
        }

        HttpPost httpPost = new HttpPost(url);
        // 设置请求参数
        if (params != null) {
            List<NameValuePair> values = getNameValuePairList(params);
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(values, Charset.forName("UTF-8"));
            httpPost.setEntity(entity);
        }

        // 设置特殊的超时时间
        if (timeout > 0) {
            Builder cfg = RequestConfig.copy(defalutRequectCfg);
            cfg.setConnectTimeout(timeout);
            httpPost.setConfig(cfg.build());
        }

        // 设置header
        if (headers != null) {
            for (Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // 执行请求
        HttpResponse response = getClient().execute(httpPost);
        return getHttpBody(response);
    }

    /**
     * 上传文件<br>
     * 使用Http-mine包上传<br>
     * 默认超时时间 @see{REQUEST_TIMEOUT}
     *
     * @param httpUrl
     * @param files
     * @param params
     * @return
     * @throws Exception
     */
    public static HttpReturnMessage upload(String httpUrl, Map<String, byte[]> files, Map<String, String> params)
            throws Exception {
        return upload(httpUrl, files, params, -1);
    }

    /**
     * * 上传文件<br>
     * 使用Http-mine包上传<br>
     *
     * @param httpUrl
     * @param files
     * @param params
     * @param timeout
     * @return
     * @throws Exception
     */
    public static HttpReturnMessage upload(String httpUrl, Map<String, byte[]> files, Map<String, String> params,
                                           int timeout) throws Exception {

        if (log.isDebugEnabled()) {
            int t = timeout;
            if (t < 0) {
                t = REQUEST_TIMEOUT;
            }
            String msg = String.format(
                    "Upload File To Http Interface , Thre Url is %s , params = [ %s ] , timeout = { %d }", httpUrl,
                    params, t);
            log.debug(msg);
        }

        //
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.RFC6532);// 设置游览器兼容模式
        builder.setBoundary(UUID.randomUUID().toString());
        builder.setCharset(Charset.forName("UTF-8"));
        // 加入普通参数
        Set<Entry<String, String>> pes = params.entrySet();
        ContentType ctype = ContentType.create("text/plain", Charset.forName("UTF-8"));
        for (Entry<String, String> ps : pes) {
            StringBody stringBody = new StringBody(ps.getValue(), ctype);
            builder.addPart(ps.getKey(), stringBody);
        }
        // 加入文件内容
        Set<Entry<String, byte[]>> es = files.entrySet();
        for (Entry<String, byte[]> bs : es) {
            builder.addBinaryBody(bs.getKey(), bs.getValue(), ContentType.MULTIPART_FORM_DATA, bs.getKey());
        }
        // 实例化客户端
        HttpPost post = new HttpPost(httpUrl);
        if (timeout > 0) {// 超时配置
            Builder reqCfg = RequestConfig.copy(defalutRequectCfg);
            reqCfg.setConnectionRequestTimeout(timeout);
            post.setConfig(reqCfg.build());
        }
        // 发送
        post.setEntity(builder.build());

        return getHttpBody(getClient().execute(post));
    }

    /**
     * 获取HttpBody
     *
     * @param resp
     * @return
     * @throws Exception
     */
    private static HttpReturnMessage getHttpBody(HttpResponse resp) throws Exception {

        int code = resp.getStatusLine().getStatusCode();
        HttpReturnMessage hrm = new HttpReturnMessage(code);

        HttpEntity rs = resp.getEntity();
        String content = EntityUtils.toString(rs, "UTF-8");
        // 关闭流
        EntityUtils.consume(rs);

        if (log.isDebugEnabled()) {
            String msg = String.format(" Get Http Response , The Http Status Code is %s , Return Message = [ %s ] , ",
                    code, content);
            log.debug(msg);
        }

        hrm.setResult(content);
        return hrm;
    }

    private static List<NameValuePair> getNameValuePairList(Map<String, String> params) {
        List<NameValuePair> listParam = new ArrayList<NameValuePair>();
        for (String key : params.keySet()) {
            listParam.add(new BasicNameValuePair(key, params.get(key)));
        }
        return listParam;
    }

    public static void main(String[] args){
        try{
           HttpReturnMessage message= HttpUtil.doGet("http://www.baidu.com/");
           System.out.print(message.getResult());
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}