/**
 * Copyright Baidu.Inc
 */
package com.zzgo.gps;

import com.zzgo.main.BaseAssistant;
import com.zzgo.main.TypeAssistant;

/**
 * 
 * @author zhoulu
 * @since 2013-1-20-下午9:14:25
 * @version 1.0
 */
public class GpsAssistant extends BaseAssistant {

	@Override
	public TypeAssistant getTypeAssistant() {
		return TypeAssistant.GPS;
	}

}
