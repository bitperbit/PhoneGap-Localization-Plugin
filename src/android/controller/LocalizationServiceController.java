package ch.hsr.sa.lbn.controller;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import ch.hsr.sa.lbn.service.IRemoteService;
import ch.hsr.sa.lbn.service.LocationService;

public class LocalizationServiceController extends Plugin {

	private final static String PREFERENCE_NAME = "locationService";

	private IRemoteService service;

	/*
	 * The execute method gets called by PhoneGap. The action String describes
	 * the exact service method which has to be called.
	 *
	 * @see org.apache.cordova.api.Plugin#execute(java.lang.String, org.json.JSONArray, java.lang.String)
	 */
	@Override
	public PluginResult execute(final String action, final JSONArray arguments, final String callBackId) {
		try {
			if(action.equals("storeItem")){
				storeItem(arguments.getString(0), arguments.getString(1));
				return new PluginResult(PluginResult.Status.OK);
			}
			if(action.equals("readItem")){
				final String value =  getPreferences().getString(arguments.getString(0),"");
				return new PluginResult(PluginResult.Status.OK, value);
			}
			if(action.equals("getState")){
				final String value = getPreferences().getString("state","");
				return new PluginResult(PluginResult.Status.OK, value);
			}
			if(action.equals("startTracking")){
				serviceAction(action);
				return new PluginResult(PluginResult.Status.OK);
			}
			if(action.equals("stopTracking")){
				serviceAction(action);
				return new PluginResult(PluginResult.Status.OK);
			}
		} catch (JSONException ex) {
			return new PluginResult(PluginResult.Status.JSON_EXCEPTION);
		}
		return new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
	}

	/*
	 * If wakeupDistance changes, the service listener has to be updated.
	 */
	private void storeItem(final String key, final String value){
		getPreferences().edit().putString(key, value).commit();
		if(key.equals("wakeupDistance")){
			serviceAction("updatePassiv");
		}
	}

	private SharedPreferences getPreferences(){
		return cordova.getActivity().getSharedPreferences(PREFERENCE_NAME, Context.MODE_MULTI_PROCESS);
	}


	/*
	 * This method binds the service to execute an action on the running service.
	 */
	private boolean serviceAction(final String action){
		if(service == null){
			Intent serviceIntent = new Intent(cordova.getActivity().getApplicationContext(), LocationService.class);
			ServiceConnection connection = new ServiceConnection(){
				public void onServiceConnected(ComponentName name, IBinder binder) {
					service = IRemoteService.Stub.asInterface(binder);
					callSpecificMehtod(action);
				}
				public void onServiceDisconnected(ComponentName name) {
					if(service != null){
						service = null;
					}
				}
			};
			return cordova.getActivity().bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
		}else{
			callSpecificMehtod(action);
			return true;
		}
	}

	private void callSpecificMehtod(final String action){
		try {
			if(action.equals("updatePassiv")){
				service.updatePassiveMode();
			}
			if(action.equals("startTracking")){
				service.startTracking();
			}
			if(action.equals("stopTracking")){
				service.stopTracking();
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
