package com.brilliance.netsign.utils;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Decription:
 * @author wangtao
 * @version 1.0
 * create on 2018/8/10.
 */
public class SSLClient extends DefaultHttpClient {
	private static  final Logger logger = LoggerFactory.getLogger(SSLClient.class);


	/**
	 * 转化为jks
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private  static KeyStore genKeyStore(String path) throws Exception {
		FileInputStream inputStream = null;
		KeyStore trustStore = null;
		try {
			File file = new File(path);

			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

   		    Certificate[] chain = new Certificate[1];
			CertificateFactory cf = null;
			X509Certificate certificate = null;
			cf = CertificateFactory.getInstance("X.509");
			inputStream = new FileInputStream(file);
			certificate = (X509Certificate) cf.generateCertificate(inputStream);
			PrivateKey privateKey = null;
			trustStore.load((InputStream) null, (char[]) null);
			trustStore.setCertificateEntry("sansec-restful", certificate);

		} finally {
			if (null != inputStream) {
				inputStream.close();
			}
		}


		return trustStore;

	}

	/**
	 *
	 * @param path ca 根证书路径
	 * @return
	 */
	public static CloseableHttpClient createSSLClientDefault(String path) {

		try {
			KeyStore trustStore = genKeyStore(path);
			//密匙库的密码

			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(trustStore, null).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}
}
