package com.yammy.meter.bengkel;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yammy.meter.R;
import com.yammy.meter.db.MySQLHelper;
import com.yammy.meter.map.MainMapBengkel;
import com.yammy.meter.map.MyCurrentBengkelListener;

/**
 * Using Content Service
 * Lecture note, Database - Percobaan 3
 * @author Ian
 *
 */
public class MainCari extends Activity {
	protected ListView bengkelList;
	protected ListAdapter adapter;
	MySQLHelper dbHelper;
	private Cursor cursor;
	private TextView data_nama;
	private TextView data_alamat;
	protected TextView data_lat;
	protected TextView data_long;
	private TextView cur_lat;
	private TextView cur_long;
	//private Button openDataMap;
	//private int id = -1;
	
	private SharedPreferences prefs;
	private String prefName = "kendaraan";
	int idkendaraan;
	Button checkin;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.isvo_activity_maincari);
        this.cur_lat = (TextView) this.findViewById(R.id.textCurLatitude);
        this.cur_long = (TextView) this.findViewById(R.id.textCurLongitude);
        this.data_nama = (TextView) this.findViewById(R.id.textDataNama);
        this.data_alamat = (TextView) this.findViewById(R.id.textDataAlamat);
        this.data_lat = (TextView) this.findViewById(R.id.textDataLatitude);
        this.data_long = (TextView) this.findViewById(R.id.textDataLongitude);
        //this.openDataMap = (Button) this.findViewById(R.id.buttonOpenMapData);
        this.checkin = (Button) this.findViewById(R.id.buttonChekin);
        checkin.setEnabled(false);
        
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean enabledGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabledGPS) {
            Toast.makeText(this, "GPS tidak ditemukan", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        MyCurrentBengkelListener locationListener = new MyCurrentBengkelListener(this.cur_lat,this.cur_long);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,locationListener);   
                
        this.bengkelList = (ListView) this.findViewById(R.id.LV1);
        this.bengkelList.setSelected(true);
        this.bengkelList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				SQLiteDatabase db = dbHelper.getReadableDatabase();
				cursor = db.rawQuery("select * from bengkel", null);
				cursor.moveToPosition(arg2);
				//Do something here, init map, or etc
				data_nama.setText(cursor.getString(1));
				data_alamat.setText(cursor.getString(2));
				data_lat.setText(cursor.getString(3));
				data_long.setText(cursor.getString(4));
				//showToastDetail(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
				
				/**
				 * open map
				 */
				/* don't start map
				Intent i = new Intent(MainCari.this, MainMap.class);
				startActivity(i);
				*/
				
				//after do something
				//id = cursor.getInt(0);
				if(idkendaraan!=0){
					checkin.setEnabled(true);
				}else{
					checkin.setEnabled(false);
				}
			}        	
        });
        dbHelper = new MySQLHelper(this);
        view();
        getVehVal();
        checkin.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				double totalJarakDitempuhKendaraan = 0.0;
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
		        /**
		         * still error
		         */
		        dbHelper = new MySQLHelper(getApplicationContext());
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				ContentValues simpan = new ContentValues();
				simpan.put("id_kendaraan", idkendaraan);
				simpan.put("jarak", totalJarakDitempuhKendaraan);
				//db.insert("perjalanan", null, simpan);
				if(db.update("perjalanan", simpan, "id_kendaraan="+idkendaraan, null) == 0){
					db.insert("perjalanan", null, simpan);
				}
				db.close();
			}
		});
    }
	
	public void but_inClick(View v){
		addData(this.data_nama.getText().toString(),this.data_alamat.getText().toString(),0,0);
		view();
	}

	private void addData(String text, String text2, int i, int j) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try{
			dbHelper.activeTable = "bengkel";
			ContentValues newValues = new ContentValues();
			newValues.put("nama", text);
			newValues.put("alamat", text2);
			newValues.put("lat", i);
			newValues.put("long", j);
			db.insert(dbHelper.activeTable, null, newValues);
		}catch(Exception e){
		}
	}

	private void view() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try{
			cursor = db.rawQuery("select * from bengkel", null);
			adapter = new android.support.v4.widget.SimpleCursorAdapter(this, R.layout.isvo_view_bengkel, cursor, new String[] {"nama","alamat","lat","long"}, new int[] {R.id.namaBengkel,R.id.alamatBengkel,R.id.textDataLatitude,R.id.textCurLongitude}, 0);
			bengkelList.setAdapter(adapter);
		}catch(Exception e){
			Log.d("Exception", e.toString());
		}
	}
	
	/**
	 * Method bantuan untuk toast lokasi
	 * @param name
	 * @param address
	 * @param lat
	 * @param longi
	 */
	
	private void getVehVal(){
		/**
		 * get value from saved preferences
		 */
		try{
		    prefs = getSharedPreferences(prefName, MODE_PRIVATE);
		    idkendaraan = Integer.parseInt(prefs.getString("idkendaraan", null));
		}catch(Exception e){
			idkendaraan = prefs.getInt("idkendaraan", 0);
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menubengkel, menu);
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.buttonOpenMapData){
			Intent i = new Intent(MainCari.this, MainMapBengkel.class);
			if(!data_nama.getText().equals(null)){
				i.putExtra("nama", data_nama.getText());
				i.putExtra("alamat", data_alamat.getText());
				i.putExtra("lat", data_lat.getText());
				i.putExtra("long", data_long.getText());
			}
			startActivity(i);
            return true;
		}else{
			return super.onOptionsItemSelected(item);
		}
	}
}
