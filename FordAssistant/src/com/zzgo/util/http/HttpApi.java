package com.zzgo.util.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import android.content.Context;

import com.zzgo.util.http.exception.AuthorizationException;
import com.zzgo.util.http.exception.XmlParserException;
import com.zzgo.util.http.exception.XmlParserParseException;

public interface HttpApi {

    abstract public String doHttpPost(Context context,String url,
            NameValuePair... nameValuePairs)
            throws AuthorizationException, XmlParserParseException,
            XmlParserException, IOException;

    abstract public HttpGet createHttpGet(String url,
            NameValuePair... nameValuePairs);

    abstract public HttpPost createHttpPost(String url,
            NameValuePair... nameValuePairs);

    abstract public HttpURLConnection createHttpURLConnectionPost(URL url,
            String boundary) throws IOException;
}
