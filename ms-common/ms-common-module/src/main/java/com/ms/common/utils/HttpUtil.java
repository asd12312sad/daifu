package com.ms.common.utils;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.MimeType;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import javax.net.ssl.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP 工具类 http get/post请求 https get/post请求
 *
 * @author zebra
 */
@Slf4j
public class HttpUtil {

    private static HttpUtil httputil = new HttpUtil();

    private HttpUtil() {

    }

    // 默认字符编码
    static String default_charset = "UTF-8";

    // 默认连接超时时间
    static int default_connecttimeout = 10000;

    // 默认读取超时时间
    static int default_readtimeout = 10000;
    private final static String CHARSET_DEFAULT = "UTF-8";






    public static String postDoDragonBead(String url) {

        String resultStr = null;

        HttpClient httpClient = new HttpClient();
        PostMethod method = new PostMethod(url);
        method.getParams().setContentCharset("utf-8");
        method.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
        try {
            int result = httpClient.executeMethod(method);

            resultStr = method.getResponseBodyAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultStr;

    }

    /**
     * post请求  编码格式默认UTF-8
     *
     * @param url 请求url
     * @return
     */
    public static HttpResult doPost(String url, Object obj) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse resp = null;

        HttpResult result = new HttpResult();

        try {
            Map<String, String> params = objectToMap(obj);
            HttpPost httpPost = new HttpPost(url);

            if (params != null && params.size() > 0) {
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(list, CHARSET_DEFAULT));
            }

            resp = httpClient.execute(httpPost);
            String body = EntityUtils.toString(resp.getEntity(), CHARSET_DEFAULT);
            int statusCode = resp.getStatusLine().getStatusCode();

            result.setStatus(statusCode);
            result.setBody(body);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != resp) {
                try {
                    resp.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String postData(String urlStr, String data) {
        return postData(urlStr, data, null);
    }

    public static String postData(String urlStr, String data, String contentType) {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(default_connecttimeout);
            conn.setReadTimeout(default_readtimeout);
            if (contentType != null) {
                conn.setRequestProperty("content-type", contentType);
            }
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), default_charset);
            if (data == null) {
                data = "";
            }
            writer.write(data);
            writer.flush();
            writer.close();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), default_charset));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\r\n");
            }
            return sb.toString();
        } catch (IOException e) {
            //
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    /**
     * 返回对象
     *
     * @param charset        编码集合， 可空。 目前支持UTF-8|UTF-16|ASCII|GB2312|GBK|ISO-8859-1|UNICODE
     *                       可空 默认UTF-8
     * @param connecttimeout 连接超时时间,不可小于10秒,最长不大于60秒, 单位为毫秒
     * @param readtimeout    读超时时间 ,不可小于10秒,最长不大于60秒, 单位为毫秒
     * @return
     */
    public static HttpUtil getInstance(String charset, int connecttimeout, int readtimeout) {
        // if (connecttimeout >= default_connecttimeout)
        // {
        // if (connecttimeout > 60000)
        // {
        // default_connecttimeout = 60000;
        // }
        // else
        // {
        // default_connecttimeout = connecttimeout;
        // }
        // }
        // if (readtimeout >= default_readtimeout)
        // {
        // if (readtimeout > 60000)
        // {
        // default_readtimeout = 60000;
        // }
        // else
        // {
        // default_readtimeout = readtimeout;
        // }
        // }
        default_connecttimeout = connecttimeout;
        default_readtimeout = readtimeout;
        if (charset != null && !"".equals(charset)) {
            charset = charset.toUpperCase();
            String[] temp = {"UTF-8", "UTF-16", "ASCII", "GB2312", "GBK", "ISO-8859-1", "UNICODE"};
            for (String s : temp) {
                if (charset.equals(s)) {
                    default_charset = charset;
                    break;
                }
            }
        }
        /*
         * log.info("编码集[" + default_charset + "],连接超时时间[" +
         * default_connecttimeout + "]毫秒,读写超时时间[" + default_readtimeout +
         * "]毫秒");
         */
        return httputil;
    }

    /**
     * @param keyPath          密钥库文件绝对路径
     * @param keyPassword      密码
     * @param keytype          密钥类型
     * @param trustkeyPath     受信库文件绝对路径
     * @param trustkeyPassword 密码
     * @param trustkeytype     密钥类型
     * @return
     */
    public static SSLContext getSSLContext(String keyPath, String keyPassword, String keytype, String trustkeyPath,
                                           String trustkeyPassword, String trustkeytype)
            throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException {
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(getkeyStore(keyPath, keyPassword, keytype), keyPassword.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(getkeyStore(trustkeyPath, trustkeyPassword, trustkeytype));
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, new SecureRandom());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        return sslContext;
    }

    /**
     * @param keyPath     密钥的绝对路径
     * @param keyPassword 密码
     * @param keytype     密钥的类型 KeyType.PKCS12.toString() or KeyType.JSK.toString()
     * @return 密钥对象
     * @Description: 获取密钥对象
     * @Check parameters by the 【caller】
     */
    public static KeyStore getkeyStore(String keyPath, String keyPassword, String keytype) {
        KeyStore keySotre = null;
        FileInputStream fis = null;
        try {
            keySotre = KeyStore.getInstance(keytype.toString());
            fis = new FileInputStream(new File(keyPath));
            keySotre.load(fis, keyPassword.toCharArray());
            fis.close();
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {

            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return keySotre;
    }

    /**
     * 把Map 转换为 key=value&key1=value1的格式
     *
     * @param map
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String changeMap2String(Map<String, Object> map) throws UnsupportedEncodingException {
        // 判断Key Value参数Map集合是否有数据，如果有处理成key=value&key2=value2格式
        if (map != null && map.size() != 0) {
            StringBuilder sb = new StringBuilder();
            String str = "";
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue().toString(), default_charset)).append("&");
            }
            if (!"".equals(sb.toString())) {
                str = "?" + sb.substring(0, sb.length() - 1);
            }
            return str;
        } else {
            return "";
        }
    }

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    public String sendHttpGet(String url, Map<String, Object> map, Map<String, String> headParams, MimeType mimeType) {

        HttpURLConnection connection = null;
        // 输出流
        OutputStream out = null;
        // 读取响应流
        BufferedReader in = null;
        // 响应结果
        StringBuilder result = new StringBuilder();
        // 判断URL是否为空
        if (url == null || "".equals(url)) {
            log.info("URL不能为空");
            return null;
        }
        try {
            // 增加参数
            url += changeMap2String(map);
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            connection = (HttpURLConnection) realUrl.openConnection();
            // 设置头信息
            connection.setRequestProperty("Connection", "Keep-Alive");
            // 接受任何类型的数据
            connection.setRequestProperty("accept", "*/*");
            // 设置请求头
            connection.setRequestProperty("Content-Type", mimeType + "; charset=" + default_charset);
            // 设置自定义头信息
            if (headParams != null && headParams.size() != 0) {
                for (Map.Entry<String, String> entry : headParams.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            // 设置不缓存
            connection.setUseCaches(false);
            // 设置连接超时时间
            connection.setConnectTimeout(default_connecttimeout);
            // 设置读取超时时间
            connection.setReadTimeout(default_readtimeout);
            // 建立实际的连接
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            } else {
                log.info("状态字:" + connection.getResponseCode());
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (Exception e) {
            log.info("http Get请求发生异常:" + e.getMessage());
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    public String sendHttpsGetByUnilateral(String url, Map<String, Object> map, Map<String, String> headParams,
                                           MimeType mimeType) {

        HttpsURLConnection connection = null;
        // 输出流
        OutputStream out = null;
        // 读取响应流
        BufferedReader in = null;
        // 响应结果
        StringBuilder result = new StringBuilder();
        // 判断URL是否为空
        if (url == null || "".equals(url)) {
            log.info("URL不能为空");
            return null;
        }
        try {
            // 增加参数
            url += changeMap2String(map);
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            connection = (HttpsURLConnection) realUrl.openConnection();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            SSLContext.setDefault(sslContext);
            SSLSocketFactory sslsocketfactory = sslContext.getSocketFactory();
            connection.setSSLSocketFactory(sslsocketfactory);
            connection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            // 设置不缓存
            connection.setUseCaches(false);
            // 发送POST请求必须设置如下两行
            connection.setDoInput(true);
            connection.setDoOutput(true);
            // 设置头信息
            connection.setRequestProperty("Connection", "Keep-Alive");
            // 接受任何类型的数据
            connection.setRequestProperty("accept", "*/*");
            // 设置请求头
            connection.setRequestProperty("Content-Type", mimeType + "; charset=" + default_charset);
            // 设置自定义头信息
            if (headParams != null && headParams.size() != 0) {
                for (Map.Entry<String, String> entry : headParams.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            connection.setConnectTimeout(default_connecttimeout);
            connection.setReadTimeout(default_readtimeout);
            // 建立实际的连接
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            } else {
                log.info("状态字:" + connection.getResponseCode());
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (Exception e) {
            log.info("https post请求发生异常:" + e.getMessage());
            return null;
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    public String sendHttpsGetByBoth(String url, Map<String, Object> map, Map<String, String> headParams,
                                     MimeType mimeType, String keyPath, String keyPassword, String keytype, String trustkeyPath,
                                     String trustkeyPassword, String trustkeytype) {

        HttpsURLConnection connection = null;
        // 输出流
        OutputStream out = null;
        // 读取响应流
        BufferedReader in = null;
        // 响应结果
        StringBuilder result = new StringBuilder();
        // 判断URL是否为空
        if (url == null || "".equals(url)) {
            log.info("URL不能为空");
            return null;
        }
        try {
            // 增加参数
            url += changeMap2String(map);
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            connection = (HttpsURLConnection) realUrl.openConnection();
            SSLContext sslContext = getSSLContext(keyPath, keyPassword, keytype, trustkeyPath, trustkeyPassword,
                    trustkeytype);
            SSLSocketFactory sslsocketfactory = sslContext.getSocketFactory();
            connection.setSSLSocketFactory(sslsocketfactory);
            // 设置不缓存
            connection.setUseCaches(false);
            // 发送POST请求必须设置如下两行
            connection.setDoInput(true);
            connection.setDoOutput(true);
            // 设置头信息
            connection.setRequestProperty("Connection", "Keep-Alive");
            // 接受任何类型的数据
            connection.setRequestProperty("accept", "*/*");
            // 设置请求头
            connection.setRequestProperty("Content-Type", mimeType + "; charset=" + default_charset);
            // 设置自定义头信息
            if (headParams != null && headParams.size() != 0) {
                for (Map.Entry<String, String> entry : headParams.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            connection.setConnectTimeout(default_connecttimeout);
            connection.setReadTimeout(default_readtimeout);
            // 建立实际的连接
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            } else {
                log.info("状态字:" + connection.getResponseCode());
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (Exception e) {
            log.info("https post请求发生异常:" + e.getMessage());
            return null;
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result.toString();
    }

    public String sendHttpPost(String url, Map<String, Object> map, Map<String, String> headParams, String str,
                               MimeType mimeType) {

        HttpURLConnection connection = null;
        // 输出流
        OutputStream out = null;
        // 读取响应流
        BufferedReader in = null;
        // 响应结果
        StringBuilder result = new StringBuilder();
        // 判断URL是否为空
        if (url == null || "".equals(url)) {
            log.info("URL不能为空");
            return null;
        }
        try {
            // 增加参数
            url += changeMap2String(map);
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            connection = (HttpURLConnection) realUrl.openConnection();
            // 设置头信息
            connection.setRequestProperty("Connection", "Keep-Alive");
            // 接受任何类型的数据
            connection.setRequestProperty("accept", "*/*");
            // 设置请求头
            connection.setRequestProperty("Content-Type", mimeType + "; charset=" + default_charset);
            // 设置自定义头信息
            if (headParams != null && headParams.size() != 0) {
                for (Map.Entry<String, String> entry : headParams.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            // 设置不缓存
            connection.setUseCaches(false);
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
            // 设置连接超时时间
            connection.setConnectTimeout(default_connecttimeout);
            // 设置读取超时时间
            connection.setReadTimeout(default_readtimeout);
            out = connection.getOutputStream();
            // 判断要发送的数据是否为空
            if (str != null && !"".equals(str)) {
                // 按字符集编码转流
                byte[] data = str.getBytes(default_charset);
                out.write(data);
            }
            out.flush();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            } else {
                log.info("状态字:" + connection.getResponseCode());
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (Exception e) {
            log.info("http Post请求发生异常:" + e.getMessage());
            return null;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return result.toString();
    }

    public String sendHttpsPostByUnilateral(String url, Map<String, Object> map, Map<String, String> headParams,
                                            String str, MimeType mimeType) {
        HttpsURLConnection connection = null;
        // 输出流
        OutputStream out = null;
        // 读取响应流
        BufferedReader in = null;
        // 响应结果
        StringBuilder result = new StringBuilder();
        // 判断URL是否为空
        if (url == null || "".equals(url)) {
            log.info("URL不能为空");
            return null;
        }
        try {
            // 增加参数
            url += changeMap2String(map);
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            connection = (HttpsURLConnection) realUrl.openConnection();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            SSLContext.setDefault(sslContext);
            SSLSocketFactory sslsocketfactory = sslContext.getSocketFactory();
            connection.setSSLSocketFactory(sslsocketfactory);
            connection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            // 设置不缓存
            connection.setUseCaches(false);
            // 发送POST请求必须设置如下两行
            connection.setDoInput(true);
            connection.setDoOutput(true);
            // 设置头信息
            connection.setRequestProperty("Connection", "Keep-Alive");
            // 接受任何类型的数据
            connection.setRequestProperty("accept", "*/*");
            // 设置请求头
            connection.setRequestProperty("Content-Type", mimeType + "; charset=" + default_charset);
            // 设置自定义头信息
            if (headParams != null && headParams.size() != 0) {
                for (Map.Entry<String, String> entry : headParams.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            connection.setConnectTimeout(default_connecttimeout);
            connection.setReadTimeout(default_readtimeout);
            out = connection.getOutputStream();
            // 判断要发送的数据是否为空
            if (str != null && !"".equals(str)) {
                // 按字符集编码转流
                byte[] data = str.getBytes(default_charset);
                out.write(data);
            }
            out.flush();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            } else {
                log.info("状态字:" + connection.getResponseCode());
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (Exception e) {
            log.info("https post请求发生异常:" + e.getMessage());
            return null;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result.toString();
    }

    public String sendHttpsPostByBoth(String url, Map<String, Object> map, Map<String, String> headParams, String str,
                                      MimeType mimeType, String keyPath, String keyPassword, String keytype, String trustkeyPath,
                                      String trustkeyPassword, String trustkeytype) throws Exception {
        HttpsURLConnection connection = null;
        // 输出流
        OutputStream out = null;
        // 读取响应流
        BufferedReader in = null;
        // 响应结果
        StringBuilder result = new StringBuilder();
        // 判断URL是否为空
        if (url == null || "".equals(url)) {
            log.info("URL不能为空");
            return null;
        }
        try {
            SSLContext sslContext = getSSLContext(keyPath, keyPassword, keytype, trustkeyPath, trustkeyPassword,
                    trustkeytype);
            // 增加参数
            url += changeMap2String(map);
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            connection = (HttpsURLConnection) realUrl.openConnection();
            SSLSocketFactory sslsocketfactory = sslContext.getSocketFactory();
            connection.setSSLSocketFactory(sslsocketfactory);
            // 设置不缓存
            connection.setUseCaches(false);
            // 发送POST请求必须设置如下两行
            connection.setDoInput(true);
            connection.setDoOutput(true);
            // 设置头信息
            connection.setRequestProperty("Connection", "Keep-Alive");
            // 接受任何类型的数据
            connection.setRequestProperty("accept", "*/*");
            // 设置请求头
            connection.setRequestProperty("Content-Type", mimeType + "; charset=" + default_charset);
            // 设置自定义头信息
            if (headParams != null && headParams.size() != 0) {
                for (Map.Entry<String, String> entry : headParams.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            connection.setConnectTimeout(default_connecttimeout);
            connection.setReadTimeout(default_readtimeout);
            out = connection.getOutputStream();
            // 判断要发送的数据是否为空
            if (str != null && !"".equals(str)) {
                // 按字符集编码转流
                byte[] data = str.getBytes(default_charset);
                out.write(data);
            }
            out.flush();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            } else {
                log.info("状态字:" + connection.getResponseCode());
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (Exception e) {
            log.info("https post请求发生异常:" + e.getMessage());
            return null;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result.toString();
    }

    /**
     * 将Object对象里面的属性和值转化成Map对象
     *
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String, String> objectToMap(Object obj) throws IllegalAccessException {
        if (obj == null) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String fieldValue = "";
            if (field.getType() == String.class || field.getType() == Integer.class || field.getType() == int.class) {
                fieldValue = field.get(obj) == null ? "" : field.get(obj).toString();
            } else {
                fieldValue = new Gson().toJson(field.get(obj));
            }
            map.put(fieldName, fieldValue);
        }
        return map;
    }
}