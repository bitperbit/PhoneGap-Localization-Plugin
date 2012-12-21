package ch.hsr.sa.lbn.service;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

public class LocationSender {

	private final static String TAG = "DEBUG";

	private final static String DEFAULT_USERNAME = "Dummy";
	private final static String DEFAULT_SERVER = "server:";
	private final static String DEFAULT_PORT="80";
	private final static String DEFAULT_PATH = "/pfad/";

	private final Context context;

	public LocationSender(final Context context){
		this.context = context;
	}

	public void send(final Location location){
		final HttpClient client = new DefaultHttpClient();
	    try {
	        HttpPost request = new HttpPost(getURL());
	        StringEntity params =new StringEntity(getLocationJSONString(location));
	        request.addHeader("content-type", "application/json");
	        request.setEntity(params);
	        client.execute(request);
	    }catch (Exception ex) {
	    	ex.printStackTrace();
	    } finally {
	        client.getConnectionManager().shutdown();
	    }
	}

	private String getURL(){
		final StringBuilder str = new StringBuilder("http://");
		str.append(getPreferences().getString("serverName", DEFAULT_SERVER));
		str.append(":");
		str.append(getPreferences().getString("serverPort", DEFAULT_PORT));
		str.append("/");
		str.append(getPreferences().getString("serverPath", DEFAULT_PATH));
		str.append("/");
		str.append("localization");
		return str.toString();
	}

	private String getLocationJSONString(final Location loc){
		final StringBuilder str = new StringBuilder();
		str.append("{\"type\":");
		str.append("\"Feature\",");
		str.append(new GeoJSONGeometry(loc.getLatitude(), loc.getLongitude()));
		str.append(",\"properties\":{");
		str.append("\"type\":\"LocalizationServicePositionProperty\",");
		str.append("\"user\":\"");
		str.append(getPreferences().getString("user", DEFAULT_USERNAME));
		str.append("\"}}");
		Log.d(TAG, str.toString());
		return str.toString();
	}

	private SharedPreferences getPreferences(){
		return context.getSharedPreferences("locationService", Context.MODE_MULTI_PROCESS);
	}
}
