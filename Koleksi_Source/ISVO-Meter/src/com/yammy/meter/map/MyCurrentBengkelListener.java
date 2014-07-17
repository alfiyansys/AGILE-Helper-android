package com.yammy.meter.map;

import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;


public class MyCurrentBengkelListener extends MyCurrentLocationListener {	
	public MyCurrentBengkelListener(TextView latitude, TextView longitude){
		this.myLat = latitude;
		this.myLong = longitude;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		this.myLat.setText(String.format("%.6f", location.getLatitude()));
		this.myLong.setText(String.format("%.6f", location.getLongitude()));
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
