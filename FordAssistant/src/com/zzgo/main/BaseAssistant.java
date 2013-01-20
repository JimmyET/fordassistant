/**
 * Copyright Baidu.Inc
 */
package com.zzgo.main;

/**
 * 
 * @author zhoulu
 * @since 2013-1-20-下午9:13:31
 * @version 1.0
 */
public abstract class BaseAssistant implements IAssistant {

	@Override
	public void openAssistant() {
	}

	@Override
	public void addAction(String actionId) {
	}

	@Override
	public void doAction(String actionId) {
	}

	@Override
	public void onResult(String actionId, IAction action) {
	}

	@Override
	public void releaseAssistant() {
	}

}
