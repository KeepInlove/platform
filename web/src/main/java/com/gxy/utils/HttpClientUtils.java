package com.gxy.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.json.JsonParser;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author pengyonglei
 * @version 1.0.0
 **/
public class HttpClientUtils {
	private static final String CHART_SET = "UTF-8";
	private static final int SOCKET_TIMEOUT = 5000; //5秒
	private static final int CONNECT_TIMEOUT = 5000; //5秒
	private static final int CONNECT_REQUEST_TIMEOUT = 5000; //5秒

	//使用内置连接池。httpclient.close()是关闭连接池
	private static CloseableHttpClient httpclient = HttpClients.createDefault();
	private static SSLConnectionSocketFactory scsf;

	static {
		try {
			scsf = new SSLConnectionSocketFactory(
					SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
					NoopHostnameVerifier.INSTANCE);
			httpclient = HttpClients.custom().setSSLSocketFactory(scsf).build();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		} catch (KeyStoreException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * get 请求
	 *
	 * @param url 请求地址
	 * @return String
	 * @throws Exception
	 */
	public static String doGet(String url) throws Exception {
		return doGet(url, null, null, SOCKET_TIMEOUT, CONNECT_TIMEOUT);
	}

	/**
	 * get 请求
	 *
	 * @param url     请求地址
	 * @param charset 返回字符集
	 * @return String
	 * @throws Exception
	 */
	public static String doGet(String url, String charset) throws Exception {
		return doGet(url, charset, null, SOCKET_TIMEOUT, CONNECT_TIMEOUT);
	}

	/**
	 * get 请求
	 *
	 * @param url     请求地址
	 * @param headers 请求头参数
	 * @return String
	 * @throws Exception
	 */
	public static String doGet(String url, Map<String, String> headers) throws Exception {
		return doGet(url, null, headers, SOCKET_TIMEOUT, CONNECT_TIMEOUT);
	}

	/**
	 * @param url     请求URL
	 * @param charset 请求返回字符编码GBK,UTF-8 等默认为UTF-8
	 * @return String
	 * @throws
	 * @Title: doGet
	 * @Description: Https Get方式访问，忽略HTTPS SSL证书
	 * 自动处理重定向
	 */
	public static String doGetIgnoreSSL(String url, String charset) throws Exception {
		HttpGet httpGet = new HttpGet(url);
		return sendGet(httpGet, charset, true);
	}


	/**
	 * @param url     请求URL
	 * @param charset 请求返回字符编码GBK,UTF-8 等默认为UTF-8
	 * @return String
	 * @throws
	 * @Title: doGet
	 * @Description: Https Get方式访问，忽略HTTPS SSL证书
	 * 自动处理重定向
	 */
	public static InputStream doGetInputStreamIgnoreSSL(String url, String charset) throws Exception {
		HttpGet httpGet = new HttpGet(url);
		return sendGetAndReturnInputStream(httpGet, charset, true);
	}

	/**
	 * get 请求
	 *
	 * @param url            请求地址
	 * @param charset        返回字符集
	 * @param socketTimeout  超时设置
	 * @param connectTimeout 超时设置
	 * @return String
	 * @throws Exception
	 */
	public static String doGet(String url, String charset, Map<String, String> headers, Integer socketTimeout, Integer connectTimeout) throws Exception {
		if (StringUtils.isEmpty(charset)) {
			charset = CHART_SET;
		}
		if (null == socketTimeout || socketTimeout <= 0) {
			socketTimeout = SOCKET_TIMEOUT;
		}
		if (null == connectTimeout || connectTimeout <= 0) {
			connectTimeout = CONNECT_TIMEOUT;
		}
		try {
			HttpGet httpGet = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(socketTimeout)
					.setConnectTimeout(connectTimeout).build();//设置请求和传输超时时间
			httpGet.setConfig(requestConfig);

			if (null != headers) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					httpGet.setHeader(entry.getKey(), entry.getValue());
				}
			}

			CloseableHttpResponse response = httpclient.execute(httpGet);
			response.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
			return getResultAsEntity(response, charset);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * post form 请求
	 * 发送post请求，参数用map接收
	 *
	 * @param url      请求地址
	 * @param paramMap 请求参数
	 * @return String
	 * @throws Exception
	 */
	public static String postForm(String url, Map<String, String> paramMap) throws Exception {
		return postForm(url, paramMap, SOCKET_TIMEOUT, CONNECT_TIMEOUT);
	}

	/**
	 * post form 请求
	 * 发送post请求，参数用map接收
	 *
	 * @param url            请求地址
	 * @param paramMap       请求参数
	 * @param socketTimeout  超时设置
	 * @param connectTimeout 超时设置
	 * @return String
	 * @throws Exception
	 */
	public static String postForm(String url, Map<String, String> paramMap, Integer socketTimeout, Integer connectTimeout) throws Exception {
		if (null == socketTimeout || socketTimeout <= 0) {
			socketTimeout = SOCKET_TIMEOUT;
		}
		if (null == connectTimeout || connectTimeout <= 0) {
			connectTimeout = CONNECT_TIMEOUT;
		}
		try {
			HttpPost post = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(socketTimeout)
					.setConnectTimeout(connectTimeout).build();//设置请求和传输超时时间
			post.setConfig(requestConfig);
			post.addHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=utf-8");
			List<NameValuePair> pairs = new ArrayList<>();
			if (null != paramMap) {
				for (Map.Entry<String, String> entry : paramMap.entrySet()) {
					pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			post.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
			CloseableHttpResponse response = httpclient.execute(post);
			return getResultAsEntity(response, CHART_SET);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * post json 请求
	 *
	 * @param url     请求地址
	 * @param content 请求参数
	 * @return String
	 * @throws Exception
	 */
	public static String postJson(String url, String content) throws Exception {
		return postJson(url, null, content, SOCKET_TIMEOUT, CONNECT_TIMEOUT, CONNECT_REQUEST_TIMEOUT);
	}

	/**
	 * post json 请求
	 *
	 * @param url            请求地址
	 * @param content        请求参数
	 * @param socketTimeout  超时设置
	 * @param connectTimeout 超时设置
	 * @return String
	 * @throws Exception
	 */
	public static String postJson(String url, String content, Integer socketTimeout, Integer connectTimeout) throws Exception {
		if (null == socketTimeout || socketTimeout <= 0) {
			socketTimeout = SOCKET_TIMEOUT;
		}
		if (null == connectTimeout || connectTimeout <= 0) {
			connectTimeout = CONNECT_TIMEOUT;
		}
		return postJson(url, null, content, socketTimeout, connectTimeout, CONNECT_REQUEST_TIMEOUT);
	}

	/**
	 * @param url
	 * @param content
	 * @param socketTimeout
	 * @param connectTimeout
	 * @param connectRequestTimeout
	 * @return
	 * @throws Exception
	 */
	public static String postJson(String url, String content, Integer socketTimeout, Integer connectTimeout, Integer connectRequestTimeout) throws Exception {
		if (null == socketTimeout || socketTimeout <= 0) {
			socketTimeout = SOCKET_TIMEOUT;
		}
		if (null == connectTimeout || connectTimeout <= 0) {
			connectTimeout = CONNECT_TIMEOUT;
		}
		if (null == connectRequestTimeout || connectRequestTimeout <= 0) {
			connectRequestTimeout = CONNECT_REQUEST_TIMEOUT;
		}
		return postJson(url, null, content, socketTimeout, connectTimeout, connectRequestTimeout);
	}

	/**
	 * post json 请求
	 *
	 * @param url     请求地址
	 * @param header  请求头信息
	 * @param content 请求参数
	 * @return String
	 * @throws Exception
	 */
	public static String postJson(String url, Map<String, String> header, String content) throws Exception {
		return postJson(url, header, content, SOCKET_TIMEOUT, CONNECT_TIMEOUT, CONNECT_REQUEST_TIMEOUT);
	}

	/**
	 * post json 请求
	 *
	 * @param url     请求地址
	 * @param header  请求头信息
	 * @param content 请求参数
	 * @return String
	 * @throws Exception
	 */
	public static String postJson(String url, Map<String, String> header, String content, int socketTimeout, int connectTimeout, int connectRequestTimeout) throws Exception {
		try {
			if (null == content || "".equals(content.trim())) {
				content = "{}";
			}
			RequestConfig requestConfig = RequestConfig.custom().
					setConnectionRequestTimeout(connectRequestTimeout)
					.setSocketTimeout(socketTimeout)
					.setConnectTimeout(connectTimeout).build();//设置请求和传输超时时间
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(requestConfig);
			httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");
			httpPost.setHeader("Accept", "application/json");
			if (null != header) {
				for (Map.Entry<String, String> entry : header.entrySet()) {
					httpPost.setHeader(entry.getKey(), entry.getValue());
				}
			}

			SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(
					SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
					NoopHostnameVerifier.INSTANCE);
			CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(scsf).build();

			StringEntity entity = new StringEntity(content, CHART_SET);//解决中文乱码问题
			entity.setContentEncoding(CHART_SET);
			entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			httpPost.setEntity(entity);
			CloseableHttpResponse response = client.execute(httpPost);//执行请求
			return getResultAsEntity(response, CHART_SET);
		} catch (Exception e) {
			throw e;
		}
	}

	public static String postFile(String url, Map<String, String> paramMap, MultipartFile file, Integer socketTimeout, Integer connectTimeout) throws IOException {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost post = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(socketTimeout != null ? socketTimeout : SOCKET_TIMEOUT)
					.setConnectTimeout(connectTimeout != null ? connectTimeout : CONNECT_TIMEOUT)
					.build();
			post.setConfig(requestConfig);

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			// 添加文件到请求体
			builder.addBinaryBody("audio_file", file.getInputStream(), ContentType.DEFAULT_BINARY, file.getOriginalFilename());
			// 添加其他表单字段
			if (paramMap != null) {
				for (Map.Entry<String, String> entry : paramMap.entrySet()) {
					builder.addTextBody(entry.getKey(), entry.getValue(), ContentType.TEXT_PLAIN);
				}
			}

			HttpEntity multipart = builder.build();
			post.setEntity(multipart);
			try (CloseableHttpResponse response = httpClient.execute(post)) {
				String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
				JSONObject jsonObject = JSON.parseObject(responseString);
				return jsonObject.toJSONString();
			}

		}
	}

	private static CloseableHttpClient httpclientIgnoreSSL = buildClientIgnoreSSL();

	/**
	 * 发送get请求
	 *
	 * @param httpGet   get请求
	 * @param charset   请求返回字符编码GBK,UTF-8 等默认为UTF-8
	 * @param ignoreSSL 忽略HTTPS SSL证书
	 * @return
	 */
	private static String sendGet(HttpGet httpGet, String charset, boolean ignoreSSL) throws Exception {
		if (StringUtils.isEmpty(charset)) {
			charset = CHART_SET;
		}
		try {
			RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECT_REQUEST_TIMEOUT).
					setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();//设置请求和传输超时时间
			httpGet.setConfig(requestConfig);

			CloseableHttpResponse response = null;
			if (ignoreSSL) {//忽略HTTPS SSL证书
				response = httpclientIgnoreSSL.execute(httpGet);
			} else {
				response = httpclient.execute(httpGet);
			}
			response.setHeader("Content-Type", "application/x-www-form-urlencoded");
			return getResultAsEntity(response, charset);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 发送get请求
	 *
	 * @param httpGet   get请求
	 * @param charset   请求返回字符编码GBK,UTF-8 等默认为UTF-8
	 * @param ignoreSSL 忽略HTTPS SSL证书
	 * @return
	 */
	private static InputStream sendGetAndReturnInputStream(HttpGet httpGet, String charset, boolean ignoreSSL) throws Exception {
		if (StringUtils.isEmpty(charset)) {
			charset = CHART_SET;
		}
		try {
			RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECT_REQUEST_TIMEOUT).
					setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();//设置请求和传输超时时间
			httpGet.setConfig(requestConfig);

			CloseableHttpResponse response = null;
			if (ignoreSSL) {//忽略HTTPS SSL证书
				response = httpclientIgnoreSSL.execute(httpGet);
			} else {
				response = httpclient.execute(httpGet);
			}
			response.setHeader("Content-Type", "application/x-www-form-urlencoded");
			return getInputStreamAsEntity(response, charset);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 忽略SSL证书
	 * 不校验HTTPS服务器证书
	 * 不校验服务器CA证书
	 */
	private static CloseableHttpClient buildClientIgnoreSSL() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null,
					new TrustStrategy() {
						// 信任所有
						@Override
						public boolean isTrusted(X509Certificate[] chain,
												 String authType) throws CertificateException {
							return true;
						}
					}).build();
			// NO_OP这个主机名验证器：是关闭主机名验证的,实现的是一个空操作，并且不会抛出javax.net.ssl.SSLException异常。
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslContext, NoopHostnameVerifier.INSTANCE);

			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 构造返回 值
	 *
	 * @param response
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	private static String getResultAsEntity(CloseableHttpResponse response, String charset) throws IOException {
		HttpEntity httpEntity = null;
		try {
			httpEntity = response.getEntity();
			return EntityUtils.toString(httpEntity, Charset.forName(charset));
		} catch (Exception e) {
			throw e;
		} finally {
			if (httpEntity != null) {
				try {
					//一定要把entity fully consume掉，否则连接池中的connection就会一直处于占用状态
					EntityUtils.consume(httpEntity);
				} catch (Exception e) {
					throw e;
				}
			}
			if (response != null) {
				try {
					//close之前, 连接状态依旧为租赁状态(leased为false), 则该连接不被复用.
					response.close();//确保连接放回连接池
				} catch (Exception e) {
					throw e;
				}
			}
		}
	}


	/**
	 * 构造返回 值
	 *
	 * @param response
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	private static InputStream getInputStreamAsEntity(CloseableHttpResponse response, String charset) throws IOException {
		HttpEntity httpEntity = null;
		try {
			httpEntity = response.getEntity();
			return httpEntity.getContent();//获取到响应信息的流
		} catch (Exception e) {
			throw e;
		} finally {
            /*if(httpEntity!=null){
                try{
                    //一定要把entity fully consume掉，否则连接池中的connection就会一直处于占用状态
                    //等同外面调用的地方主动关闭流 InputStream.close();
                    EntityUtils.consume(httpEntity);
                }catch (Exception e){
                    throw e;
                }
            }*/
			if (response != null) {
				try {
					//close之前, 连接状态依旧为租赁状态(leased为false), 则该连接不被复用.
					response.close();//确保连接放回连接池
				} catch (Exception e) {
					throw e;
				}
			}
		}
	}


	/**
	 * SSL协议发送post请求，参数用map接收
	 *
	 * @param url   地址
	 * @param param 参数
	 * @return 返回值
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public String postDataSSL(String url, Map<String, String> param, Map<String, String> headers) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		String result = null;
		CloseableHttpClient httpClient = getHttpClientSSL();
		HttpPost post = new HttpPost(url);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		if (param != null) {
			for (Map.Entry<String, String> entry : param.entrySet()) {
				pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		CloseableHttpResponse response = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));

			if (headers != null)
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					post.setHeader(entry.getKey(), entry.getValue());
				}

			response = httpClient.execute(post);
			if (response != null && response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				result = entityToString(entity);
			}
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	private String entityToString(HttpEntity entity) throws IOException {
		String result = null;
		if (entity != null) {
			long lenth = entity.getContentLength();
			if (lenth != -1 && lenth < 2048) {
				result = EntityUtils.toString(entity, "UTF-8");
			} else {
				InputStreamReader reader1 = new InputStreamReader(entity.getContent(), "UTF-8");
				CharArrayBuffer buffer = new CharArrayBuffer(2048);
				char[] tmp = new char[1024];
				int l;
				while ((l = reader1.read(tmp)) != -1) {
					buffer.append(tmp, 0, l);
				}
				result = buffer.toString();
			}
		}
		return result;
	}

	public CookieStore cookieStore = new BasicCookieStore();


	private CloseableHttpClient getHttpClientSSL() throws KeyManagementException, NoSuchAlgorithmException {
		SSLContext sslcontext = null;
		try {
			sslcontext = createIgnoreVerifySSL();
		} catch (Exception ex) {
			//logger.error(ex.getMessage());
		}
		Registry<ConnectionSocketFactory> socketFactoryRegistry;
		if (sslcontext != null)
			socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", new SSLConnectionSocketFactory(sslcontext))
					.build();
		else
			socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.build();

		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		HttpClients.custom().setConnectionManager(connManager);

		RequestConfig defaultRequestConfig = RequestConfig.custom()
				.setSocketTimeout(30000)
				.setConnectTimeout(30000)
				.setConnectionRequestTimeout(30000)
				.setStaleConnectionCheckEnabled(true)
				.build();

		CloseableHttpClient client = HttpClients.custom()
				.setConnectionManager(connManager)
				.setDefaultCookieStore(cookieStore)
				.setDefaultRequestConfig(defaultRequestConfig)
				.build();
		return client;
	}

	/**
	 * 绕过验证
	 *
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc = SSLContext.getInstance("SSLv3");

		// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(
					X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(
					X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		sc.init(null, new TrustManager[]{trustManager}, null);
		return sc;
	}

}
