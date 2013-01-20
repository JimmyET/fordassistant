/**
 * Copyright Baidu.Inc
 */
package com.zzgo.main;

import android.os.AsyncTask;

/**
 * 基础动作
 * @author zhoulu
 * @since 2013-1-20-下午9:19:52
 * @version 1.0
 */
public abstract class BaseAction extends AsyncTask<Void, Void, Void> implements IAction {
	public static final String ACTION_HTTP = "action_http";//联网动作

	@Override
	public void doAction() {
		//执行操作
		execute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		//TODO 线程操作
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		//TODO 返回操作结果
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		//TODO 操作进度
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		//TODO 操作取消
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//TODO 预操作
	}
	
}
