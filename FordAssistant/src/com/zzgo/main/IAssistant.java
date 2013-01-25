/**
 * Copyright Baidu.Inc
 */
package com.zzgo.main;

/**
 * 
 * @author zhoulu
 * @since 2013-1-20-下午9:12:55
 * @version 1.0
 */
public interface IAssistant {

	public void openAssistant();
	
	public void doAction(int action);
	
	public void onResult(int action);
	
	public void releaseAssistant();
	
	public void setIAssistantHandler(IAssistantHandler handler);
	
	public TypeAssistant getTypeAssistant();
}
