package ch.hsr.sa.lbn.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

public class LocationSleepManager {

	private static final int DEFAULT_SLEEP_TIME_WINDOW = 300000;
	private static final int DEFAULT_DISTANCE_TRESHOLD = 500;
	private static final int MILISECONDS_MULTIPLIAKTOR = 1000;

	private final List <Location> locationList;
	private final LocationService service;

	public LocationSleepManager(final LocationService service) {
		this.locationList = new ArrayList<Location>();
		this.service = service;
	}

	public void add(final Location location){
		float maxDistance = 0l;
		locationList.add(location);
		for(Location loc : locationList){
			if(location.distanceTo(loc) > maxDistance){
				maxDistance = location.distanceTo(loc);
			}
		}

		long timeDiff = location.getTime() - locationList.get(0).getTime();
		if(timeDiff >= getSleepTimeWindow()){
			if(maxDistance < getDistanceTreshold()){
				service.activatePassiveMode();
			}else{
				for(int i=0; i<locationList.size(); i++){
					if(locationList.get(i).getTime() - location.getTime() >= getSleepTimeWindow()){
						locationList.remove(i);
					}
				}
			}
		}
	}

	private int getSleepTimeWindow(){
		if(getPreferences().getString("sleepTimeWindow", null) != null){
			return Integer.valueOf(getPreferences().getString("sleepTimeWindow", null))
					* MILISECONDS_MULTIPLIAKTOR;
		}
		return DEFAULT_SLEEP_TIME_WINDOW;
	}

	private int getDistanceTreshold(){
		if(getPreferences().getString("sleepDistanceTreshold", null) != null){
			return Integer.valueOf(getPreferences().getString("sleepDistanceTreshold", null));
		}
		return DEFAULT_DISTANCE_TRESHOLD;
	}

	private SharedPreferences getPreferences(){
		return service.getSharedPreferences("locationService", Context.MODE_MULTI_PROCESS);
	}
}
