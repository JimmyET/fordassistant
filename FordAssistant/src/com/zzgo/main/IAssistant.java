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
	
	public void addAction(String actionId);
	
	public void doAction(String actionId);
	
	public void onResult(String actionId, IAction action);
	
	public void releaseAssistant();
}
