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

	public static final int ACTION_HTTP = -100;
	
	public IAssistantHandler mAssistantHandler;
	
	@Override
	public void openAssistant() {
	}

	@Override
	public void doAction(int action) {
		switch(action){
		case ACTION_HTTP:
			break;
		}
	}

	@Override
	public void onResult(int action) {
		mAssistantHandler.handlerAssistantResult(action, this);
	}

	@Override
	public void releaseAssistant() {
	}

	@Override
	public void setIAssistantHandler(IAssistantHandler handler) {
		mAssistantHandler = handler;
	}

	public IAssistantHandler getAssistantHandler() {
		return mAssistantHandler;
	}

	
}
