/**
 * Copyright Baidu.Inc
 */
package com.zzgo.tts;

import com.zzgo.main.BaseAssistant;
import com.zzgo.main.TypeAssistant;

/**
 * 
 * @author zhoulu
 * @since 2013-1-20-下午9:14:59
 * @version 1.0
 */
public class TTSAssistant extends BaseAssistant {

	@Override
	public TypeAssistant getTypeAssistant() {
		return TypeAssistant.TTS;
	}

}
