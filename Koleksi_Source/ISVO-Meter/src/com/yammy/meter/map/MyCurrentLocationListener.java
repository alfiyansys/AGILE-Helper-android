package com.yammy.meter.map;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;


public class MyCurrentLocationListener implements LocationListener {
	public String myLocation;
	protected TextView myLat;
	protected TextView myLong;
	
	public MyCurrentLocationListener(TextView latitude, TextView longitude){
		this.myLat = latitude;
		this.myLong = longitude;
	}
	
	public MyCurrentLocationListener(){
		
	}
	
	@Override
	public void onLocationChanged(Location location) {
		
	}

	@Override
	public void onProviderDisabled(String location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

}
