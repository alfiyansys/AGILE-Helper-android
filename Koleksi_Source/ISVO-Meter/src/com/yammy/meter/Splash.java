package com.yammy.meter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.yammy.meter.db.MySQLHelper;

public class Splash extends Activity {
	protected MySQLHelper dbhelper;
	/*
	 * using shared preference for determine first time execution
	 * if first time, create database then show tutorial activity,
	 * else enter main activity
	 */
	SharedPreferences prefs;
	private String prefName = "exec";
	int numExec = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.isvo_activity_splash);
		Thread logoTimer = new Thread(){
			public void run(){
				try{
					Intent menuIntent;					
					getNumberExec();
					if(numExec < 1){
						prefs = getSharedPreferences(prefName, MODE_PRIVATE);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putInt("numExec", numExec+1);
						editor.commit();
						sleep(500);
						menuIntent = new Intent(Splash.this, Tutorial.class);
					}else{
						sleep(1000);
						menuIntent = new Intent(Splash.this, MainActivity.class);
					}
					startActivity(menuIntent);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					finish();
				}
			}
		};
		dbhelper = new MySQLHelper(getApplicationContext());
		logoTimer.start();
	}
	
	private void getNumberExec(){
    	/**
         * get value from saved preferences
         */
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        numExec = prefs.getInt("numExec", 0);
	}
}
