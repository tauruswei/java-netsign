package com.brilliance.netsign.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Decription: 请求发送
 * @author wangtao
 * @version 1.0
 * create on 2018/12/18.
 */
public class RestTemplateTools {


/*
	public static String post(String url, String requestInfo) {
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		//创建自定义的httpclient对象
		ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(RestTemplateTools.httpClient());

		RestTemplate restTemplate = new RestTemplate(requestFactory);

		HttpEntity<String> formEntity = new HttpEntity<String>(requestInfo, headers);

		return restTemplate.postForObject(url, formEntity, String.class);
	}*/

	public static RestTemplate restTemplate(String cacertPath) {
		//创建自定义的httpclient对象
		ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(RestTemplateTools.httpClient(cacertPath));
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}


	public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc = SSLContexts.custom()
				.useTLS()
				.build();


		//SSLContext.getInstance("TLSv1.1");


		// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		sc.init(null, new TrustManager[]{trustManager}, null);
		return sc;
	}

	private static HttpClient httpClient(String cacertPath) {

		String[] tls = {"TLSv1", "TLSv1.1", "TLSv1.2"};
		String[] Suites = {"TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",

				"TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",

				"TLS_RSA_WITH_AES_256_CBC_SHA256",
				"TLS_RSA_WITH_AES_128_CBC_SHA256"};

//		SSLConnectionSocketFactory sslsf = SSLClient.createSSLClientDefault(cacertPath);

		// 支持HTTP、HTTPS
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
//				.register("https", sslsf)
				.build();
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
		connectionManager.setMaxTotal(200);
		connectionManager.setDefaultMaxPerRoute(100);
		connectionManager.setValidateAfterInactivity(2000);

		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(65000) // 服务器返回数据(response)的时间，超时抛出read timeout
				.setConnectTimeout(5000) // 连接上服务器(握手成功)的时间，超时抛出connect timeout
				.setConnectionRequestTimeout(1000)// 从连接池中获取连接的超时时间，超时抛出ConnectionPoolTimeoutException
				.build();
		return HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setConnectionManager(connectionManager).build();
	}


}
