package com.yammy.meter.map;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yammy.meter.R;

public class MainMapBengkel extends FragmentActivity implements LocationListener{
	GoogleMap map;
	private LocationManager locationManager;
	Bundle bundle = null;
	LatLng data_pos = null;
	TextView jarak;
	Marker currentLocation,dataLocation;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.isvo_map_bengkel);
        this.jarak = (TextView) this.findViewById(R.id.map_bengkel_length);
        
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(result != ConnectionResult.SUCCESS){
        	GooglePlayServicesUtil.getErrorDialog(result, MainMapBengkel.this, 1).show();
        }else{
	        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	        GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getBaseContext());
	        
	        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			map.setIndoorEnabled(false);
			bundle = getIntent().getExtras();
			try{		        
	        	data_pos = new LatLng(Double.parseDouble(bundle.getString("lat")),Double.parseDouble(bundle.getString("long")));
	         	Toast.makeText(getBaseContext(), bundle.getString("lat")+","+bundle.getString("long"), Toast.LENGTH_LONG).show();
			}catch(NumberFormatException e){
				Toast.makeText(getBaseContext(), "Mencari lokasi saat ini", Toast.LENGTH_SHORT).show();
			}
			
	        /**
	         * mulai edan dari ini ke bawah
	         */
	        LocationManager service = (LocationManager) this.getSystemService(LOCATION_SERVICE);
	        boolean enabledGPS = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
	        if (!enabledGPS) {
	            Toast.makeText(this, "GPS tidak ditemukan", Toast.LENGTH_LONG).show();
	            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	            startActivity(intent);
	        }
	        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);        
	        
	        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
	        if(location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
	            Toast.makeText(getApplicationContext(), "Current Location : "+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_LONG).show();
	            drawMarker(location);
	        }else{
	        	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	        }
        }
	}
	
	private void drawMarker(Location myLocation){
		//map.clear();
		try{
			this.currentLocation.remove();
		}catch(NullPointerException e){
		}
		
		//current location
		LatLng currentPosition = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
		this.currentLocation = map.addMarker(new MarkerOptions().position(currentPosition).snippet("Lat:" + myLocation.getLatitude() + "Lng:"+ myLocation.getLongitude()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("ME"));
		
		//data location
		float[] lengthdata = new float[3];
		try{
			this.dataLocation = map.addMarker(new MarkerOptions().position(data_pos).title(bundle.getString("nama")).snippet(bundle.getString("alamat")));
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(data_pos, 18.0f));
			Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(), data_pos.latitude, data_pos.longitude, lengthdata);
			this.jarak.setText("Jarak : "+String.format("%.2f", lengthdata[0])+" m");
		}catch(Exception e){
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 18.0f));
			this.jarak.setText("Jarak : 0 m");
		}		
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

	@Override
	public void onLocationChanged(Location location) {
		drawMarker(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
}
