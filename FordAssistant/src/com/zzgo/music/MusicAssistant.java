/**
 * Copyright Baidu.Inc
 */
package com.zzgo.music;

import com.zzgo.main.BaseAssistant;
import com.zzgo.main.TypeAssistant;

/**
 * 
 * @author zhoulu
 * @since 2013-1-20-下午9:14:43
 * @version 1.0
 */
public class MusicAssistant extends BaseAssistant {

	public static final int ACTION_MUSIC_OPEN = 1;
	public static final int ACTION_MUSIC_NEXT = ACTION_MUSIC_OPEN + 1;
	public static final int ACTION_MUSIC_PRE = ACTION_MUSIC_OPEN + 2;
	public static final int ACTION_MUSIC_CLOSE = ACTION_MUSIC_OPEN + 3;
	
	@Override
	public TypeAssistant getTypeAssistant() {
		return TypeAssistant.MUSIC;
	}

	@Override
	public void openAssistant() {
		super.openAssistant();
	}

	@Override
	public void doAction(int action) {
		super.doAction(action);
		switch(action){
		case ACTION_MUSIC_OPEN:
			break;
		case ACTION_MUSIC_NEXT:
			break;
		case ACTION_MUSIC_PRE:
			break;
		case ACTION_MUSIC_CLOSE:
			break;
		}
	}
	
	
}
