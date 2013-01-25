/**Ford Motor Company
 * September 2012
 * Elizabeth Halash
 */

package com.zzgo.main;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ford.syncV4.exception.SyncException;
import com.ford.syncV4.exception.SyncExceptionCause;
import com.ford.syncV4.proxy.SyncProxyALM;
import com.ford.syncV4.proxy.interfaces.IProxyListenerALM;
import com.ford.syncV4.proxy.rpc.AddCommandResponse;
import com.ford.syncV4.proxy.rpc.AddSubMenuResponse;
import com.ford.syncV4.proxy.rpc.AlertResponse;
import com.ford.syncV4.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.ford.syncV4.proxy.rpc.DeleteCommandResponse;
import com.ford.syncV4.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.ford.syncV4.proxy.rpc.DeleteSubMenuResponse;
import com.ford.syncV4.proxy.rpc.EncodedSyncPDataResponse;
import com.ford.syncV4.proxy.rpc.GenericResponse;
import com.ford.syncV4.proxy.rpc.OnButtonEvent;
import com.ford.syncV4.proxy.rpc.OnButtonPress;
import com.ford.syncV4.proxy.rpc.OnCommand;
import com.ford.syncV4.proxy.rpc.OnDriverDistraction;
import com.ford.syncV4.proxy.rpc.OnEncodedSyncPData;
import com.ford.syncV4.proxy.rpc.OnHMIStatus;
import com.ford.syncV4.proxy.rpc.OnPermissionsChange;
import com.ford.syncV4.proxy.rpc.OnTBTClientState;
import com.ford.syncV4.proxy.rpc.PerformInteractionResponse;
import com.ford.syncV4.proxy.rpc.ResetGlobalPropertiesResponse;
import com.ford.syncV4.proxy.rpc.SetGlobalPropertiesResponse;
import com.ford.syncV4.proxy.rpc.SetMediaClockTimerResponse;
import com.ford.syncV4.proxy.rpc.ShowResponse;
import com.ford.syncV4.proxy.rpc.SpeakResponse;
import com.ford.syncV4.proxy.rpc.SubscribeButtonResponse;
import com.ford.syncV4.proxy.rpc.UnsubscribeButtonResponse;
import com.zzgo.gps.GpsAssistant;
import com.zzgo.music.MusicAssistant;
import com.zzgo.tts.TTSAssistant;

public class AppLinkService extends Service implements IProxyListenerALM, IAssistantHandler {

	private static final String TAG = "AppLinkService";
	private BluetoothAdapter mBtAdapter;
	private SyncProxyALM proxy = null;
	private static AppLinkService instance = null;

	private AssistantHelper assistantHelper;
	private GpsAssistant gpsAssistant;
	private MusicAssistant musicAssistant;
	private TTSAssistant ttsAssistant;

	private AssistantModel mAssistantModel;

	public static AppLinkService getInstance() {
		return instance;
	}

	public SyncProxyALM getProxy() {
		return proxy;
	}

	public void setCurrentActivity(MainActivity currentActivity) {
		mAssistantModel.setCurrentActivity(currentActivity);
	}

	public void onCreate() {
		super.onCreate();
		instance = this;
		mAssistantModel = new AssistantModel(this);
		initAssistants();
	}

	private void initAssistants() {
		assistantHelper = new AssistantHelper();

		gpsAssistant = new GpsAssistant();
		gpsAssistant.setIAssistantHandler(this);
		assistantHelper.registerAssistant(gpsAssistant);

		musicAssistant = new MusicAssistant();
		musicAssistant.setIAssistantHandler(this);
		assistantHelper.registerAssistant(musicAssistant);

		ttsAssistant = new TTSAssistant();
		ttsAssistant.setIAssistantHandler(this);
		assistantHelper.registerAssistant(ttsAssistant);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			mBtAdapter = BluetoothAdapter.getDefaultAdapter();
			if (mBtAdapter != null) {
				if (mBtAdapter.isEnabled()) {
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
				mAssistantModel.setProxy(proxy);
			} catch (SyncException e) {
				e.printStackTrace();
				// error creating proxy, returned proxy = null
				if (proxy == null) {
					stopSelf();
				}
			}
		}
	}

	public void onDestroy() {
		disposeSyncProxy();
		mAssistantModel.clearlockscreen();
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
			mAssistantModel.clearlockscreen();
		}
	}

	public void onProxyClosed(String info, Exception e) {
		mAssistantModel.clearlockscreen();

		if ((((SyncException) e).getSyncExceptionCause() != SyncExceptionCause.SYNC_PROXY_CYCLED)) {
			if (((SyncException) e).getSyncExceptionCause() != SyncExceptionCause.BLUETOOTH_DISABLED) {
				Log.v(TAG, "reset proxy in onproxy closed");
				reset();
			}
		}
	}

	public void reset() {
		try {
			proxy.resetProxy();
		} catch (SyncException e1) {
			e1.printStackTrace();
			// something goes wrong, & the proxy returns as null, stop the
			// service.
			// do not want a running service with a null proxy
			if (proxy == null) {
				stopSelf();
			}
		}
	}

	public void onOnHMIStatus(OnHMIStatus notification) {
		mAssistantModel.holdHMIStatus(notification);
	}

	public boolean getLockScreenStatus() {
		return mAssistantModel.isLockScreenUp();
	}

	public void onError(String info, Exception e) {

	}

	public void onGenericResponse(GenericResponse response) {

	}

	public void onOnCommand(OnCommand notification) {

	}

	public void onAddCommandResponse(AddCommandResponse response) {

	}

	public void onAddSubMenuResponse(AddSubMenuResponse response) {

	}

	public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse response) {

	}

	public void onAlertResponse(AlertResponse response) {

	}

	public void onDeleteCommandResponse(DeleteCommandResponse response) {

	}

	public void onDeleteInteractionChoiceSetResponse(DeleteInteractionChoiceSetResponse response) {

	}

	public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {

	}

	public void onEncodedSyncPDataResponse(EncodedSyncPDataResponse response) {

	}

	public void onPerformInteractionResponse(PerformInteractionResponse response) {

	}

	public void onResetGlobalPropertiesResponse(ResetGlobalPropertiesResponse response) {

	}

	public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response) {

	}

	public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {

	}

	public void onShowResponse(ShowResponse response) {

	}

	public void onSpeakResponse(SpeakResponse response) {

	}

	public void onOnButtonEvent(OnButtonEvent notification) {

	}

	public void onOnButtonPress(OnButtonPress notification) {

	}

	public void onSubscribeButtonResponse(SubscribeButtonResponse response) {

	}

	public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {

	}

	public void onOnPermissionsChange(OnPermissionsChange notification) {

	}

	public void onOnDriverDistraction(OnDriverDistraction notification) {
		mAssistantModel.holdDriverDistraction(notification);
	}

	public void onOnEncodedSyncPData(OnEncodedSyncPData notification) {

	}

	public void onOnTBTClientState(OnTBTClientState notification) {

	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void handlerAssistantResult(int action, IAssistant assistant) {
		switch (assistant.getTypeAssistant()) {
		case GPS:
			break;
		}
	}

}
