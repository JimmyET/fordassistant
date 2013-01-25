/**
 * Copyright Baidu.Inc
 */
package com.zzgo.main;

/**
 * 
 * @author zhoulu
 * @since 2013-1-24-下午5:21:36
 * @version 1.0
 */
public enum TypeAssistant {
	GPS("gps"), 
	MUSIC("music"), 
	TTS("music");

	private String type;

	TypeAssistant(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
