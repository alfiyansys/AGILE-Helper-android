package com.yammy.meter;

import java.util.Calendar;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.yammy.meter.bengkel.MainCari;
import com.yammy.meter.db.MySQLHelper;
import com.yammy.meter.kendaraan.MainKendaraan;

public class MainActivity extends FragmentActivity implements LocationListener{
	GoogleMap map;
	private LocationManager locationManager;
	LatLng user_pos;
	TextView lat,longi,nopol,jarak;
	Button start, stop, reset;
	Marker currentLocation;
	boolean requestFocus = false;
	boolean recording = false;
	Polyline line;
	double jarakTotal = 0.0;
	MySQLHelper dbHelper;
	
	int idkendaraan;
	private SharedPreferences prefs;
	private String prefName = "kendaraan";
	//ready for some trigger, done
	double totalJarakDitempuhKendaraan = 0.0;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        
        setContentView(R.layout.isvo_activity_main);
        lat = (TextView) this.findViewById(R.id.main_lat);
        longi = (TextView) this.findViewById(R.id.main_long);
        nopol = (TextView) this.findViewById(R.id.main_nopol);
        jarak = (TextView) this.findViewById(R.id.main_jarak);
        start = (Button) this.findViewById(R.id.buttonStartRec);
        stop = (Button) this.findViewById(R.id.buttonStopRec);
        reset = (Button) this.findViewById(R.id.buttonClrMapMain);
        stop.setEnabled(false);
        reset.setEnabled(false);
        Location location = null;
        
        getVehVal();
        
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(result != ConnectionResult.SUCCESS){
        	GooglePlayServicesUtil.getErrorDialog(result, MainActivity.this, 1).show();
        }else{			
			/**
			 * try to get current location via GPS
			 * pending : path recording using Polyline & get length
			 */
			
			locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
	        boolean enabledGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	        if (!enabledGPS) {
	            requestGPS();
	            finish();
	        }
	        
	        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_main)).getMap();
	        GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getBaseContext());
	        
	        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			map.setIndoorEnabled(false);

	        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
	        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);        
	        
	        try{
		        user_pos = new LatLng(location.getLatitude(),location.getLongitude());
		        if(location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
		            Toast.makeText(getApplicationContext(), "Current Location : "+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_LONG).show();
		            drawMarker(location);
		        }else{
		        	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
		        	drawMarker(location);
		        }
	        }catch(NullPointerException e){
	        	//requestGPS();
	        }
        }
        
        start.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				recording = true;	
				Toast.makeText(getBaseContext(), "Mulai merekam perjalanan", Toast.LENGTH_SHORT).show();
				stop.setEnabled(true);
				start.setEnabled(false);
				reset.setEnabled(false);
				jarakTotal = 0.0;
			}
		});
        stop.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				recording = false;			
				Toast.makeText(getBaseContext(), "Selesai merekam perjalanan", Toast.LENGTH_SHORT).show();
				Toast.makeText(getBaseContext(), "Perjalanan terakhir "+jarakTotal+" m", Toast.LENGTH_SHORT).show();
				/**
				 * then save to database
				 */
				try{
					dbHelper = new MySQLHelper(getApplicationContext());
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					ContentValues simpan = new ContentValues();
					simpan.put("id_kendaraan", idkendaraan);
					simpan.put("jarak", jarakTotal);
					db.insert("perjalanan", null, simpan);
					Toast.makeText(getBaseContext(), "Perjalanan tersimpan", Toast.LENGTH_SHORT).show();
					db.close();
				}catch(Exception e){	
					Toast.makeText(getBaseContext(), "Tidak bisa menyimpan perjalanan", Toast.LENGTH_SHORT).show();
				}
				
				//partial reset
				stop.setEnabled(false);
				start.setEnabled(true);
				reset.setEnabled(true);
				jarakTotal = 0.0;
				
				checkJarak();
			}
		});
        reset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				map.clear();
				jarakTotal = 0.0;
				jarak.setText(null);
			}
		});
        checkJarak();
    }
    
    private void drawMarker(Location location){
    	try{
    		this.currentLocation.remove();
    	}catch(NullPointerException e){
    	}
		LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
		this.lat.setText(String.format("%.6f", location.getLatitude()));
		this.longi.setText(String.format("%.6f", location.getLongitude()));
		this.currentLocation = map.addMarker(new MarkerOptions().position(currentPosition).snippet("Lat:" + location.getLatitude() + "Lng:"+ location.getLongitude()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("ME"));
		if(recording == true || requestFocus == false){
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 17.0f));
			requestFocus = true;
		}
    }
    
    
    /**
     * Main Activity Options menu goes here
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent i;
    	if(item.getItemId() == R.id.mainItemMenu1){
    		i = new Intent(MainActivity.this, MainCari.class);
    		startActivity(i);
            return true;
    	}else if(item.getItemId() == R.id.mainItemMenu2){
    		i = new Intent(MainActivity.this, MainKendaraan.class);
    		startActivity(i);
            return true;
    	}else if(item.getItemId() == R.id.mainItemMenu3){
    		i = new Intent(MainActivity.this, About.class);
    		startActivity(i);
            return true;
    	}else if(item.getItemId() == R.id.mainItemMenu4){
    		i = new Intent(MainActivity.this, Tutorial.class);
    		startActivity(i);
            return true;
    	}else{
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    private void requestGPS(){
		Toast.makeText(this, "Pastikan GPS sudah aktif", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }
    
    
    @Override
    protected void onResume() {
    	super.onResume();
        getVehVal();
        checkJarak();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        try{
        	locationManager.removeUpdates(this);
        }catch(Exception e){        	
        }
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	try{
        	locationManager.removeUpdates(this);
        }catch(Exception e){        	
        }
    }
    
    private void checkJarak(){
    	double maxval;
    	dbHelper = new MySQLHelper(getApplicationContext());
    	SQLiteDatabase jdb = dbHelper.getReadableDatabase();
    	Cursor cursor = jdb.rawQuery("SELECT maxlength FROM kendaraan WHERE _id="+idkendaraan, null);
    	if(cursor.moveToFirst()){
    		maxval = cursor.getDouble(0);
    		double temp;
        	temp = (totalJarakDitempuhKendaraan % maxval);
        	//simple if, need 'mbengkel' data for details
        	//cursor = jdb.rawQuery("SELECT jarak FROM latest_mbengkel WHERE id_kendaraan="+idkendaraan, null);
        	if(temp >= maxval*90/100){
        		showAlert();
        	}
    	}    	
    	jdb.close();
    }
    
    private void showAlert(){
		final Dialog myDialog = new Dialog(this);
		myDialog.setContentView(R.layout.isvo_dialog_low_oil);
		myDialog.setCancelable(true);
		myDialog.setTitle("Peringatan!");
		Button batal = (Button) myDialog.findViewById(R.id.dialog_low_oil_cancel);
		Button cari = (Button) myDialog.findViewById(R.id.dialog_low_oil_cari);
		myDialog.show();
		cari.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, MainCari.class);
        		startActivity(i);
			}
		});
		batal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.cancel();
			}
		});
    } 
    
    private void getVehVal(){
    	/**
         * get value from saved preferences
         */
    	try{
	        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
	        nopol.setText(prefs.getString("nopol", null));
	        idkendaraan = Integer.parseInt(prefs.getString("idkendaraan", null));
	        start.setEnabled(true);
	    }catch(Exception e){
	    	idkendaraan = prefs.getInt("idkendaraan", 0);
    		start.setEnabled(false);
    	}
    	
        
		dbHelper = new MySQLHelper(getApplicationContext());
    	SQLiteDatabase jdb = dbHelper.getReadableDatabase();
		try{
        	Cursor cursor = jdb.rawQuery("SELECT SUM(jarak) FROM perjalanan WHERE id_kendaraan="+idkendaraan, null);
	        if (cursor.moveToFirst()) {
	        	try{
	        		totalJarakDitempuhKendaraan = Double.parseDouble(String.format("%.2f", cursor.getString(0)));
	        	}catch(NumberFormatException e){
	        		totalJarakDitempuhKendaraan = Double.parseDouble(cursor.getString(0));
	        	}catch(Exception e){
	        		totalJarakDitempuhKendaraan = cursor.getDouble(0);
	        	}
	        }else{
	        	totalJarakDitempuhKendaraan = 0.0;
	        }
	        Toast.makeText(getBaseContext(), "Sudah ditempuh : "+totalJarakDitempuhKendaraan+" m", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
        	Toast.makeText(getBaseContext(), "Belum pernah melakukan perjalanan", Toast.LENGTH_SHORT).show();
        }
        jdb.close();
    }

    /**
     * run AFTER location update
     */
	@Override
	public void onLocationChanged(Location arg0) {
		/**
		 * currentPosition update after drawMarker exec, so determine length & draw linehere
		 * this.currentPosition vs arg0
		 */
		try{
			if(recording){
				float[] jaraktemp = new float[3];
				line = map.addPolyline(new PolylineOptions().add(new LatLng(this.currentLocation.getPosition().latitude, this.currentLocation.getPosition().longitude), new LatLng(arg0.getLatitude(), arg0.getLongitude())).width(5).color(Color.BLUE).geodesic(true));
				Location.distanceBetween(this.currentLocation.getPosition().latitude, this.currentLocation.getPosition().longitude, arg0.getLatitude(), arg0.getLongitude(), jaraktemp);
				this.jarakTotal = this.jarakTotal + jaraktemp[0];
				jarak.setText(String.format("%.2f", this.jarakTotal)+" m");
			}
		}catch(Exception e){
			Toast.makeText(getBaseContext(), "Menunggu update lokasi", Toast.LENGTH_SHORT).show();
		}
		//update location marker
		drawMarker(arg0);
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
