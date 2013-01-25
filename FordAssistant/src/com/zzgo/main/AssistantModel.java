/**
 * Copyright Baidu.Inc
 */
package com.zzgo.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ford.syncV4.exception.SyncException;
import com.ford.syncV4.proxy.SyncProxyALM;
import com.ford.syncV4.proxy.rpc.OnDriverDistraction;
import com.ford.syncV4.proxy.rpc.OnHMIStatus;
import com.ford.syncV4.proxy.rpc.enums.ButtonName;
import com.ford.syncV4.proxy.rpc.enums.DriverDistractionState;
import com.ford.syncV4.proxy.rpc.enums.TextAlignment;
import com.ford.syncV4.util.DebugTool;

/**
 * 
 * @author zhoulu
 * @since 2013-1-24-下午6:17:26
 * @version 1.0
 */
public class AssistantModel {
	private static String TAG = "AssistantModel";
	public int autoIncCorrId = 0;
	private TypeModel mTypeModel;
	private boolean driverdistrationNotif = false;
	private boolean lockscreenUP = false;
	
	private SyncProxyALM proxy;
	private Context mContext;
	
	public AssistantModel(Context context){
		this(context, null);
	}
	
	public AssistantModel(Context context, SyncProxyALM proxy){
		mContext = context;
		this.proxy = proxy;
	}

	public void setProxy(SyncProxyALM proxy) {
		this.proxy = proxy;
	}

	public void holdHMIStatus(OnHMIStatus notification){
		switch (notification.getSystemContext()) {
		case SYSCTXT_MAIN:
			break;
		case SYSCTXT_VRSESSION:
			break;
		case SYSCTXT_MENU:
			break;
		default:
			return;
		}

		switch (notification.getAudioStreamingState()) {
		case AUDIBLE:
			// play audio if applicable
			break;
		case NOT_AUDIBLE:
			// pause/stop/mute audio if applicable
			break;
		default:
			return;
		}

		switch (notification.getHmiLevel()) {
		case HMI_FULL:
			if (driverdistrationNotif == false) {
				showLockScreen();
			}
			if (notification.getFirstRun()) {
				// setup app on SYNC
				// send welcome message if applicable
				try {
					proxy.show("this is the first", "show command", TextAlignment.CENTERED, ++autoIncCorrId);
				} catch (SyncException e) {
					DebugTool.logError("Failed to send Show", e);
				}
				// send addcommands
				// subscribe to buttons
				subButtons();
				if (MainActivity.getInstance() != null) {
					setCurrentActivity(MainActivity.getInstance());
				}
			} else {
				try {
					proxy.show("SyncProxy is", "Alive", TextAlignment.CENTERED, ++autoIncCorrId);
				} catch (SyncException e) {
					DebugTool.logError("Failed to send Show", e);
				}
			}
			break;
		case HMI_LIMITED:
			if (driverdistrationNotif == false) {
				showLockScreen();
			}
			break;
		case HMI_BACKGROUND:
			if (driverdistrationNotif == false) {
				showLockScreen();
			}
			break;
		case HMI_NONE:
			Log.i("hello", "HMI_NONE");
			driverdistrationNotif = false;
			clearlockscreen();
			break;
		default:
			return;
		}
	}
	
	public void setCurrentActivity(Activity activity) {
		mContext = activity;
	}

	public void showLockScreen() {
		// only throw up lockscreen if main activity is currently on top
		// else, wait until onResume() to throw lockscreen so it doesn't
		// pop-up while a user is using another app on the phone
		if (mContext != null && mContext instanceof MainActivity) {
			if (((MainActivity)mContext).isActivityonTop()) {
				if (LockScreenActivity.getInstance() == null) {
					Intent i = new Intent(mContext, LockScreenActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
					mContext.startActivity(i);
				}
			}
		}
		lockscreenUP = true;
	}

	public void clearlockscreen() {
		if (LockScreenActivity.getInstance() != null) {
			LockScreenActivity.getInstance().exit();
		}
		lockscreenUP = false;
	}
	
	public boolean isLockScreenUp(){
		return lockscreenUP;
	}

	public void holdDriverDistraction(OnDriverDistraction notification) {
		driverdistrationNotif = true;
		// Log.i(TAG, "dd: " + notification.getStringState());
		if (notification.getState() == DriverDistractionState.DD_OFF) {
			Log.i(TAG, "clear lock, DD_OFF");
			clearlockscreen();
		} else {
			Log.i(TAG, "show lockscreen, DD_ON");
			showLockScreen();
		}
	}
	
	public void subButtons() {
		try {
			proxy.subscribeButton(ButtonName.OK, autoIncCorrId++);
			proxy.subscribeButton(ButtonName.SEEKLEFT, autoIncCorrId++);
			proxy.subscribeButton(ButtonName.SEEKRIGHT, autoIncCorrId++);
			proxy.subscribeButton(ButtonName.TUNEUP, autoIncCorrId++);
			proxy.subscribeButton(ButtonName.TUNEDOWN, autoIncCorrId++);
			proxy.subscribeButton(ButtonName.PRESET_1, autoIncCorrId++);
			proxy.subscribeButton(ButtonName.PRESET_2, autoIncCorrId++);
			proxy.subscribeButton(ButtonName.PRESET_3, autoIncCorrId++);
			proxy.subscribeButton(ButtonName.PRESET_4, autoIncCorrId++);
			proxy.subscribeButton(ButtonName.PRESET_5, autoIncCorrId++);
			proxy.subscribeButton(ButtonName.PRESET_6, autoIncCorrId++);
			proxy.subscribeButton(ButtonName.PRESET_7, autoIncCorrId++);
			proxy.subscribeButton(ButtonName.PRESET_8, autoIncCorrId++);
			proxy.subscribeButton(ButtonName.PRESET_9, autoIncCorrId++);
			proxy.subscribeButton(ButtonName.PRESET_0, autoIncCorrId++);
		} catch (SyncException e) {
		}
	}
}
