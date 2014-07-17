package com.yammy.meter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.yammy.meter.bengkel.DataBengkel;


public class MySQLHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "data.db";
	public String activeTable;
	private static final int DB_VER = 1;
	public MySQLHelper(Context context){
		super(context, DB_NAME, null, DB_VER);		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/**
		 * sql - exec, per-table
		 */		
		//1st table
		String sql = "create table bengkel(" +
				"_id integer primary key autoincrement," +
				"nama text not null," +
				"alamat text not null," +
				"lat text not null," +
				"long text not null" +
				")";
		db.execSQL(sql);
		Log.d("Data", sql);
		
		//2nd table
		sql = "create table kendaraan(" +
				"_id integer primary key autoincrement," +
				"produk text not null," +
				"nopol text not null," +
				"maxlength number" +
				")";
		db.execSQL(sql);
		Log.d("Data", sql);
		
		//3rd table
		sql = "create table perjalanan(" +
				"_id integer primary key autoincrement," +
				"id_kendaraan integer not null," +
				"jarak real not null," +
				"waktu DATETIME DEFAULT CURRENT_TIMESTAMP" +
				")";
		db.execSQL(sql);
		Log.d("Data", sql);
		
		//4th table
		sql = "create table latest_mbengkel(" +
				"id_kendaraan integer not null," +
				"jarak real not null," +
				"waktu DATETIME DEFAULT CURRENT_TIMESTAMP" +
				")";
		db.execSQL(sql);
		Log.d("Data", sql);
		
		/**
		 * Insert default data to table
		 */
		//tabel bengkel
		//mutable data
		DataBengkel mainData = new DataBengkel();
		mainData.executeDB(db);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
				
	}
}
