/**Ford Motor Company
 * September 2012
 * Elizabeth Halash
 */

package com.zzgo.main;

import com.ford.syncV4.exception.SyncException;
import com.ford.syncV4.exception.SyncExceptionCause;
import com.ford.syncV4.proxy.SyncProxyALM;
import com.ford.syncV4.proxy.interfaces.IProxyListenerALM;
import com.ford.syncV4.proxy.rpc.*;
import com.ford.syncV4.proxy.rpc.enums.ButtonName;
import com.ford.syncV4.proxy.rpc.enums.DriverDistractionState;
import com.ford.syncV4.proxy.rpc.enums.TextAlignment;
import com.ford.syncV4.util.DebugTool;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AppLinkService extends Service implements IProxyListenerALM{

	String TAG = "hello";
	public int autoIncCorrId = 0;
	private BluetoothAdapter mBtAdapter;
	private SyncProxyALM proxy = null;
	private static AppLinkService instance = null;
	private MainActivity currentUIActivity;
	private boolean driverdistrationNotif = false;
	private boolean lockscreenUP = false;
	
	public static AppLinkService getInstance() {
		return instance;
	}
	
	public MainActivity getCurrentActivity() {
		return currentUIActivity;
	}
	
	public SyncProxyALM getProxy() {
		return proxy;
	}

	public void setCurrentActivity(MainActivity currentActivity) {
		this.currentUIActivity = currentActivity;
	}
	
	public void onCreate() {
		super.onCreate();
		instance = this;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
        	mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    		if (mBtAdapter != null){
    			if (mBtAdapter.isEnabled()){
    				startProxy();
    			}
    		}
		}
        if (MainActivity.getInstance() != null) {
        	setCurrentActivity(MainActivity.getInstance());
        }
			
        return START_STICKY;
	}
	
	public void startProxy() {
		if (proxy == null) {
			try {
				proxy = new SyncProxyALM(this, "Hello AppLink", true);
			} catch (SyncException e) {
				e.printStackTrace();
				//error creating proxy, returned proxy = null
				if (proxy == null){
					stopSelf();
				}
			}
		}
	}
	
	public void onDestroy() {
		disposeSyncProxy();
		clearlockscreen();
		instance = null;
		super.onDestroy();
	}
	
	public void disposeSyncProxy() {
		if (proxy != null) {
			try {
				proxy.dispose();
			} catch (SyncException e) {
				e.printStackTrace();
			}
			proxy = null;
			clearlockscreen();
		}
	}
	
	public void onProxyClosed(String info, Exception e) {
		clearlockscreen();
		
		if((((SyncException) e).getSyncExceptionCause() != SyncExceptionCause.SYNC_PROXY_CYCLED))
		{
			if (((SyncException) e).getSyncExceptionCause() != SyncExceptionCause.BLUETOOTH_DISABLED) 
			{
				Log.v(TAG, "reset proxy in onproxy closed");
				reset();
			}
		}
	}

   public void reset(){
	   try {
			proxy.resetProxy();
		} catch (SyncException e1) {
			e1.printStackTrace();
			//something goes wrong, & the proxy returns as null, stop the service.
			//do not want a running service with a null proxy
			if (proxy == null){
				stopSelf();
			}
		}
   }
   
   public void onOnHMIStatus(OnHMIStatus notification) {

		  switch(notification.getSystemContext()) {
				 case SYSCTXT_MAIN:
					   break;
				 case SYSCTXT_VRSESSION:
					   break;
				 case SYSCTXT_MENU:
					   break;
				 default:
					   return;
		  }
		  
		  switch(notification.getAudioStreamingState()) {
				 case AUDIBLE:
					//play audio if applicable
					   break;
				 case NOT_AUDIBLE:
					//pause/stop/mute audio if applicable
					   break;
				 default:
					   return;
		  }
		  
		  switch(notification.getHmiLevel()) {
				 case HMI_FULL:
					 if (driverdistrationNotif == false) {showLockScreen();}
					 if(notification.getFirstRun()) {
						   //setup app on SYNC
						   //send welcome message if applicable
						 	try {
								proxy.show("this is the first", "show command", TextAlignment.CENTERED, ++autoIncCorrId);
							} catch (SyncException e) {
								DebugTool.logError("Failed to send Show", e);
							}				
						    //send addcommands
						    //subscribe to buttons
						 	subButtons();
						 	if (MainActivity.getInstance() != null) {
					        	setCurrentActivity(MainActivity.getInstance());
					        }
						}
					 else{
						 try {
								proxy.show("SyncProxy is", "Alive", TextAlignment.CENTERED, ++autoIncCorrId);
							} catch (SyncException e) {
								DebugTool.logError("Failed to send Show", e);
							}
					 }
					   break;
				 case HMI_LIMITED:
					 if (driverdistrationNotif == false) {showLockScreen();}
					   break;
				 case HMI_BACKGROUND:
					 if (driverdistrationNotif == false) {showLockScreen();}
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

public void showLockScreen() {
	//only throw up lockscreen if main activity is currently on top
	//else, wait until onResume() to throw lockscreen so it doesn't 
	//pop-up while a user is using another app on the phone
	if(currentUIActivity != null) {
		if(currentUIActivity.isActivityonTop() == true){
			if(LockScreenActivity.getInstance() == null) {
				Intent i = new Intent(this, LockScreenActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
				startActivity(i);
			}
		}
	}
	lockscreenUP = true;		
}

private void clearlockscreen() {
	if(LockScreenActivity.getInstance() != null) {  
		LockScreenActivity.getInstance().exit();
	}
	lockscreenUP = false;
}

public boolean getLockScreenStatus() {return lockscreenUP;}

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
	} catch (SyncException e) {}
}

public void onError(String info, Exception e) {
	// TODO Auto-generated method stub
	
}

public void onGenericResponse(GenericResponse response) {
	// TODO Auto-generated method stub
	
}

public void onOnCommand(OnCommand notification) {
	// TODO Auto-generated method stub
	
}

public void onAddCommandResponse(AddCommandResponse response) {
	// TODO Auto-generated method stub
	
}

public void onAddSubMenuResponse(AddSubMenuResponse response) {
	// TODO Auto-generated method stub
	
}

public void onCreateInteractionChoiceSetResponse(
		CreateInteractionChoiceSetResponse response) {
	// TODO Auto-generated method stub
	
}

public void onAlertResponse(AlertResponse response) {
	// TODO Auto-generated method stub
	
}

public void onDeleteCommandResponse(DeleteCommandResponse response) {
	// TODO Auto-generated method stub
	
}

public void onDeleteInteractionChoiceSetResponse(
		DeleteInteractionChoiceSetResponse response) {
	// TODO Auto-generated method stub
	
}

public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {
	// TODO Auto-generated method stub
	
}

public void onEncodedSyncPDataResponse(EncodedSyncPDataResponse response) {
	// TODO Auto-generated method stub
	
}

public void onPerformInteractionResponse(PerformInteractionResponse response) {
	// TODO Auto-generated method stub
	
}

public void onResetGlobalPropertiesResponse(
		ResetGlobalPropertiesResponse response) {
	// TODO Auto-generated method stub
	
}

public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response) {

}

public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {
	// TODO Auto-generated method stub
	
}

public void onShowResponse(ShowResponse response) {
	// TODO Auto-generated method stub
	
}

public void onSpeakResponse(SpeakResponse response) {
	// TODO Auto-generated method stub
	
}

public void onOnButtonEvent(OnButtonEvent notification) {
	// TODO Auto-generated method stub
	
}

public void onOnButtonPress(OnButtonPress notification) {
	// TODO Auto-generated method stub
	
}

public void onSubscribeButtonResponse(SubscribeButtonResponse response) {
	// TODO Auto-generated method stub
	
}

public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {
	// TODO Auto-generated method stub
	
}

public void onOnPermissionsChange(OnPermissionsChange notification) {
	// TODO Auto-generated method stub
	
}

public void onOnDriverDistraction(OnDriverDistraction notification) {
	driverdistrationNotif = true;
	//Log.i(TAG, "dd: " + notification.getStringState());
	if (notification.getState() == DriverDistractionState.DD_OFF)
	{
		Log.i(TAG,"clear lock, DD_OFF");
		clearlockscreen();
	} else {
		Log.i(TAG,"show lockscreen, DD_ON");
		showLockScreen();
	}
}

public void onOnEncodedSyncPData(OnEncodedSyncPData notification) {
	// TODO Auto-generated method stub
	
}

public void onOnTBTClientState(OnTBTClientState notification) {
	// TODO Auto-generated method stub
	
}

@Override
public IBinder onBind(Intent intent) {
	// TODO Auto-generated method stub
	return null;
}


}
