package ch.hsr.sa.lbn.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;


public class LocationService extends Service implements LocationListener{

	private final static String TAG = "DEBUG";

	private final static long TIME_BETWEEN_MEASUREMENT_PASSIVE = 10000;
	private final static long TIME_BETWEEN_MEASUREMENT_ACTIVE = 5000;
	private final static int DEFAULT_WAKE_UP_DISTANCE = 500;
	private final static int MIN_MEASUREMENT_TIME = 20000;
	private final static int DEFAULT_MIN_DISTANCE = 1;

	private LocationManager manager;
	private LocationSender sender;
	private LocationSleepManager sleepManager;

	private Thread listenerThread;

	private Location lastSentLocation;

	private boolean isFirstLocation = true;
	private boolean isActive = false;
	private boolean isStopped = false;

	@Override
	public void onCreate() {
		Log.d(TAG, "Location Service has been created");
		initLocalization();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "Location Service has been started");
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "Location Service has been destroyed");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return new IRemoteService.Stub(){

			public void startTracking() throws RemoteException {
				isFirstLocation = true;
				isStopped = false;
				initLocalization();
			}

			public void stopTracking() throws RemoteException {
				stopTrackingService();
			}

			public void updatePassiveMode() throws RemoteException {
				if(!isActive){
					activatePassiveMode();
				}
			}
		};
	}

	private void initLocalization(){
		manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		sender = new LocationSender(getApplicationContext());
		activatePassiveMode();
	}

	private synchronized void stopTrackingService(){
		isStopped = false;
		manager.removeUpdates(this);
		setServiceStatus("Stopped");
	}

	protected void activatePassiveMode(){
		Log.d(TAG, "passive mode");
		isActive = false;
		createListener(false);
	}

	private void activateActiveMode(){
		Log.d(TAG, "active mode");
		isActive = true;
		sleepManager = new LocationSleepManager(this);
		createListener(true);
	}

	private void createListener(final boolean isActive){
		final LocationListener listener = this;
		listenerThread = new Thread(new Runnable(){
			public void run() {
				Looper.prepare();
				if (isActive) {
					manager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER,
							TIME_BETWEEN_MEASUREMENT_ACTIVE, 1, listener);
					setServiceStatus("Active");
				} else {
					manager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							TIME_BETWEEN_MEASUREMENT_PASSIVE,
							getWakeUpDistance(), listener);
					setServiceStatus("Passive");
				}
				Looper.loop();
			}
		});
		listenerThread.start();
	}

	public void onLocationChanged(final Location loc) {
		if(isStopped){
			manager.removeUpdates(this);
			manager = null;
		}
		if(isActive){
			sleepManager.add(loc);
			if(lastSentLocation == null){
				sendLocation(loc);
			}else{
				if(loc.getTime() - lastSentLocation.getTime() >= getMinTimeFilter() &&
						loc.distanceTo(lastSentLocation) >= getMinDistanceFilter()){
					sendLocation(loc);
				}
			}
		}else{
			Log.d(TAG, "Location changed Passiv");
			if(isFirstLocation){
				isFirstLocation = false;
			}else{
				activateActiveMode();
			}
		}
	}

	private void sendLocation(final Location loc){
		Log.d(TAG, "Send location");
		sender.send(loc);
		lastSentLocation = loc;
	}

	private void setServiceStatus(String state){
		getSharedPreferences("locationService", Context.MODE_MULTI_PROCESS)
			.edit().putString("state", state).commit();
	}

	/*
	 * The wake-up distance means the distance which the device has to travel
	 * that the GPS-Gatherer will be awakened.
	 */
	private int getWakeUpDistance(){
		if(getStoredProperty("wakeupDistance")!=null){
			return Integer.valueOf(getStoredProperty("wakeupDistance"));
		}
		return DEFAULT_WAKE_UP_DISTANCE;
	}

	/*
	 * The measurement time is the time between each location measurement.
	 * The default value is 5000 milliseconds and should not be under
	 * this time interval.
	 */
	private int getMinTimeFilter(){
		if(getStoredProperty("minTimeFilter") != null){
			int value = Integer.valueOf(getStoredProperty("minTimeFilter")) * 1000;
			return value < MIN_MEASUREMENT_TIME ? value : MIN_MEASUREMENT_TIME;
		}else{
			return MIN_MEASUREMENT_TIME;
		}
	}

	/*
	 * The minimum distance describes the distance between the current and the last
	 * Location point. If it's less then the minimum distance, the location will not
	 * be tracked.
	 */
	private int getMinDistanceFilter(){
		if(getStoredProperty("minDistanceFilter")!=null){
			return Integer.valueOf(getStoredProperty("minDistanceFilter"));
		}
		return DEFAULT_MIN_DISTANCE;
	}

	private String getStoredProperty(String key){
		return getSharedPreferences("locationService", Context.MODE_MULTI_PROCESS).
				getString(key, null);
	}

	public void onProviderDisabled(String provider) {
		Log.d(TAG, "Provider disabled " + provider);
	}

	public void onProviderEnabled(String provider) {
		Log.d(TAG, "Provider enabled " + provider);
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
}
