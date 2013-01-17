package com.zzgo.util.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;

import com.zzgo.util.LogUtil;
import com.zzgo.util.http.exception.AuthorizationException;
import com.zzgo.util.http.exception.XmlParserException;
import com.zzgo.util.http.exception.XmlParserParseException;

abstract public class AbstractHttpApi implements HttpApi {

	private static final String TAG = AbstractHttpApi.class.getCanonicalName();

	private static final String CLIENT_VERSION_HEADER = "User-Agent";
	private static final int TIMEOUT = 60;
	private static final int TIMEOUT_2 = 30;
	private static final String DEFAULT_CLIENT_VERSION = "v1.0";
	private final DefaultHttpClient mHttpClient;
	private final String mClientVersion;

	public AbstractHttpApi(DefaultHttpClient httpClient, String clientVersion) {
		mHttpClient = httpClient;
		if (clientVersion != null) {
			mClientVersion = clientVersion;
		} else {
			mClientVersion = DEFAULT_CLIENT_VERSION;
		}
	}

	public DefaultHttpClient getDefaultHttpClient() {
		return mHttpClient;
	}

	/**
	 * 执行post
	 */
	public String doHttpPost(Context context, String url, NameValuePair... nameValuePairs)
			throws AuthorizationException, XmlParserParseException, XmlParserException, IOException {
		LogUtil.d(TAG, "doHttpPost: " + url);
		HttpPost httpPost = createHttpPost(url, nameValuePairs);

		HttpResponse response = executeHttpRequest(context, httpPost);
		LogUtil.d(TAG, "executed HttpRequest for: " + httpPost.getURI().toString());

		switch (response.getStatusLine().getStatusCode()) {
		case 200:
			try {
				return EntityUtils.toString(response.getEntity());
			} catch (ParseException e) {
				throw new XmlParserParseException(e.getMessage());
			}

		case 401:
			response.getEntity().consumeContent();
			throw new AuthorizationException(response.getStatusLine().toString());

		case 404:
			response.getEntity().consumeContent();
			throw new XmlParserException(response.getStatusLine().toString());

		default:
			response.getEntity().consumeContent();
			throw new XmlParserException(response.getStatusLine().toString());
		}
	}

	/**
	 * execute() an httpRequest catching exceptions and returning null instead.
	 * 
	 * @param httpRequest
	 * @return
	 * @throws IOException
	 */
	public HttpResponse executeHttpRequest(Context context, HttpRequestBase httpRequest) throws IOException {
		LogUtil.d(TAG, "executing HttpRequest for: " + httpRequest.getURI().toString());
		try {
			// mHttpClient.getConnectionManager().closeExpiredConnections();
			DefaultHttpClient client = createHttpClientSimple(context);
			if (context != null) {
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				if (!wifiManager.isWifiEnabled()) {
					// 用APN的方式去获取
					try {
						Uri uri = Uri.parse("content://telephony/carriers/preferapn"); // 获取当前正在使用的APN接入点
						Cursor mCursor = context.getContentResolver().query(uri, null, null, null, null);
						if (mCursor != null) {
							boolean b = mCursor.moveToNext(); // 游标一直第一条记录，当前只有一条
							if (b) {
								String proxyStr = mCursor.getString(mCursor.getColumnIndex("proxy"));// 有可能报错
								if (proxyStr != null && proxyStr.trim().length() > 0) {
									HttpHost proxy = new HttpHost(proxyStr, 80);
									client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
								}
							}
							mCursor.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
			HttpResponse httpResponse = client.execute(httpRequest);
			// FlowRateManagement.addFlowRate(TingApplication.getAppContext(),
			// FlowRateManagement.FLOW_RATE_AUTO,
			// httpResponse.getEntity().getContentLength());
			return httpResponse;
		} catch (IOException e) {
			httpRequest.abort();
			throw e;
		}
	}

	/**
	 * 根据URL地址和参数创建一个httpget对象 NameValuePair可以传入key-value对应的参数
	 */
	public HttpGet createHttpGet(String url, NameValuePair... nameValuePairs) {
		LogUtil.d(TAG, "creating HttpGet for: " + url);
		url = url.replace(" ", "");
		HttpGet httpGet = null;
		if (nameValuePairs != null && nameValuePairs.length > 0) {
			String query = URLEncodedUtils.format(stripNulls(nameValuePairs), HTTP.UTF_8);
			System.out.println("query = " + query);
			httpGet = new HttpGet(url + "?" + query);
		} else {
			httpGet = new HttpGet(url);
		}
		// httpGet.addHeader(CLIENT_VERSION_HEADER, mClientVersion);
		LogUtil.d(TAG, "Created: " + httpGet.getURI());
		return httpGet;
	}

	/**
	 * 根据URL地址和参数创建一个httpget对象 NameValuePair可以传入key-value对应的参数
	 */
	public HttpGet createHttpGet(String url, String encode, NameValuePair... nameValuePairs) {
		LogUtil.d(TAG, "creating HttpGet for: " + url);
		HttpGet httpGet = null;
		if (nameValuePairs != null && nameValuePairs.length > 0) {
			String query = URLEncodedUtils.format(stripNulls(nameValuePairs), encode);
			System.out.println("query = " + query);
			httpGet = new HttpGet(url + "?" + query);
		} else {
			httpGet = new HttpGet(url);
		}
		// httpGet.addHeader(CLIENT_VERSION_HEADER, mClientVersion);
		LogUtil.d(TAG, "Created: " + httpGet.getURI());
		return httpGet;
	}

	public HttpPost createHttpPost(String url, NameValuePair... nameValuePairs) {
		LogUtil.d(TAG, "creating HttpPost for: " + url);
		HttpPost httpPost = new HttpPost(url);
		// httpPost.addHeader(CLIENT_VERSION_HEADER, mClientVersion);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(stripNulls(nameValuePairs), HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			throw new IllegalArgumentException("Unable to encode http parameters.");
		}
		LogUtil.d(TAG, "Created: " + httpPost);
		return httpPost;
	}

	public HttpURLConnection createHttpURLConnectionPost(URL url, String boundary) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setConnectTimeout(TIMEOUT * 1000);
		conn.setRequestMethod("POST");

		conn.setRequestProperty(CLIENT_VERSION_HEADER, mClientVersion);
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

		// zhl modify.流量计算只计流量:下载,试听,图片,歌词;
		// 这种请求数据量很小,目前的流量机制有效率问题,不能这样每次都作记录;
		// FlowRateManagement.addFlowRate(TingApplication.getAppContext(),
		// FlowRateManagement.FLOW_RATE_AUTO, conn.getContentLength());

		return conn;
	}

	private List<NameValuePair> stripNulls(NameValuePair... nameValuePairs) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (int i = 0; i < nameValuePairs.length; i++) {
			NameValuePair param = nameValuePairs[i];
			if (param.getValue() != null) {
				LogUtil.d(TAG, "Param: " + param);
				params.add(param);
			}
		}
		return params;
	}

	/**
	 * Create a thread-safe client. This client does not do redirecting, to
	 * allow us to capture correct "error" codes.
	 * 
	 * @return HttpClient
	 */
	public static final DefaultHttpClient createHttpClient() {
		// // Sets up the http part of the service.
		// final SchemeRegistry supportedSchemes = new SchemeRegistry();
		//
		// // Register the "http" protocol scheme, it is required
		// // by the default operator to look up socket factories.
		// final SocketFactory sf = PlainSocketFactory.getSocketFactory();
		// supportedSchemes.register(new Scheme("http", sf, 80));
		// supportedSchemes.register(new Scheme("https", SSLSocketFactory
		// .getSocketFactory(), 443));
		//
		// // Set some client http client parameter defaults.
		// final HttpParams httpParams = createHttpParams(TIMEOUT);
		// HttpClientParams.setRedirecting(httpParams, false);
		//
		// final ClientConnectionManager ccm = new ThreadSafeClientConnManager(
		// httpParams, supportedSchemes);
		// return new DefaultHttpClient(ccm, httpParams);
		return new DefaultHttpClient();
	}

	public static final DefaultHttpClient createHttpClientSimple(Context context) {
		final HttpParams httpParams = createHttpParams(context, TIMEOUT);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);

		return httpclient;
	}

	/**
	 * 根据当前网络状态填充代理
	 * 
	 * @param context
	 * @param httpParams
	 */
	public static void fillProxy(final Context context, final HttpParams httpParams) {
		if (context == null) {
			return;
		}

		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled()) {
			return;
		}

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null || networkInfo.getExtraInfo() == null) {
			return;
		}
		String info = networkInfo.getExtraInfo().toLowerCase(); // 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
		// 先根据网络apn信息判断,并进行 proxy 自动补齐
		if (info != null) {
			if (info.startsWith("cmwap") || info.startsWith("uniwap") || info.startsWith("3gwap")) {
				HttpHost proxy = new HttpHost("10.0.0.172", 80);
				httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
				return;
			} else if (info.startsWith("ctwap")) {
				HttpHost proxy = new HttpHost("10.0.0.200", 80);
				httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
				return;
			} else if (info.startsWith("cmnet") || info.startsWith("uninet") || info.startsWith("ctnet")
					|| info.startsWith("3gnet")) {
				return;
			} // else fall through
		} // else fall through

		// 如果没有 apn 信息，则根据 proxy代理判断。
		// 由于android 4.2 对 "content://telephony/carriers/preferapn"
		// 读取进行了限制，我们通过系统接口获取。

		// 绝大部分情况下不会走到这里
		// 此两个方法是deprecated的，但在4.2下仍可用
		String defaultProxyHost = android.net.Proxy.getDefaultHost();
		int defaultProxyPort = android.net.Proxy.getDefaultPort();

		if (defaultProxyHost != null && defaultProxyHost.length() > 0) {
			/*
			 * 无法根据 proxy host 还原 apn 名字 这里不设置 mApn
			 */
			if ("10.0.0.172".equals(defaultProxyHost.trim())) {
				// 当前网络连接类型为cmwap || uniwap
				HttpHost proxy = new HttpHost("10.0.0.172", defaultProxyPort);
				httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
			} else if ("10.0.0.200".equals(defaultProxyHost.trim())) {
				HttpHost proxy = new HttpHost("10.0.0.200", 80);
				httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
			} else {
			}
		} else {
			// 其它网络都看作是net
		}
	}

	public static final DefaultHttpClient createHttpClientSimple2(Context context) {
		final HttpParams httpParams = createHttpParams(context, TIMEOUT_2);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);
		return httpclient;
	}

	/**
	 * Create the default HTTP protocol parameters.
	 */
	private static final HttpParams createHttpParams(Context context, int timeOut) {
		final HttpParams params = new BasicHttpParams();

		// Turn off stale checking. Our connections break all the time anyway,
		// and it's not worth it to pay the penalty of checking every time.
		HttpConnectionParams.setStaleCheckingEnabled(params, false);

		HttpConnectionParams.setConnectionTimeout(params, timeOut * 1000);
		HttpConnectionParams.setSoTimeout(params, timeOut * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		HttpProtocolParams.setUserAgent(params, DEFAULT_CLIENT_VERSION);

		fillProxy(context, params);

		return params;
	}

}
