package com.zzgo.util.http.parse.json;

import org.json.JSONException;

import com.zzgo.util.http.parse.BaseObject;

/**
 * 预留接口 TODO 连接下载完成后,用来解析
 * 
 * @author fuliqiang 2010-9-18
 * @param <T>
 */
public interface IJsonParser<T extends BaseObject> {
	public abstract T parse(String jsonString) throws JSONException;
}
