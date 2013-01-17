/**
 * Copyright Baidu.Inc
 */
package com.zzgo.util.http.parse.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.zzgo.util.LogUtil;
import com.zzgo.util.http.parse.BaseObject;

/**
 * Json的解析
 * 
 * @author zhoulu
 * @since 2013-1-17-下午1:54:33
 * @version 1.0
 */
public abstract class BaseJsonParser<T extends BaseObject> implements IJsonParser<T> {

	private static final boolean DEBUG = LogUtil.LOGGABLE;
	private static final String TAG = BaseJsonParser.class.getCanonicalName();

	@Override
	public T parse(String jsonString) throws JSONException {
		if (DEBUG) {
			LogUtil.d(TAG, "parse json string := " + jsonString);
		}
		JSONObject obj = new JSONObject(jsonString);
		return parseInner(obj);
	}

	abstract protected T parseInner(final JSONObject jsonObject) throws JSONException;
}
