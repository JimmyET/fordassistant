/**
 * Copyright Baidu.Inc
 */
package com.zzgo.main;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author zhoulu
 * @since 2013-1-20-下午9:31:35
 * @version 1.0
 */
public class AssistantHelper {

	private Map<String, IAssistant> assistantList = new HashMap<String, IAssistant>();
	
	public void registerAssistant(IAssistant assistant) {
		if(!assistantList.containsKey(assistant.getTypeAssistant().getType())){
			assistantList.put(assistant.getTypeAssistant().getType(), assistant);
		}
	}

	public void unregisterAssistant(IAssistant assistant) {
		assistantList.remove(assistant.getTypeAssistant().getType());
		assistant.releaseAssistant();
	}

}
