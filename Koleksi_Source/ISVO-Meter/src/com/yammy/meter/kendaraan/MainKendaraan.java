package com.yammy.meter.kendaraan;

import org.apache.commons.lang3.StringEscapeUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yammy.meter.R;
import com.yammy.meter.db.MySQLHelper;

public class MainKendaraan extends Activity {
	protected ListAdapter adapter;
	protected String iddata;
	private TextView noPol;
	private TextView jenisKendaraan;
	private TextView jarakDitempuh;
	private TextView jarakMax;
	private TextView lastMbengkel;
	private ListView listKendaraan;
	Button activate;
	private Cursor cursor;
	MySQLHelper dbHelper;
	
	private SharedPreferences prefs;
	private String prefName = "kendaraan";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.isvo_activity_kendaraan);
        this.noPol = (TextView) this.findViewById(R.id.textDataNopol);
        this.jenisKendaraan = (TextView) this.findViewById(R.id.textDataJenisKendaraan);
        this.jarakDitempuh = (TextView) this.findViewById(R.id.textDataJarakDitempuhKendaraan);
        this.jarakMax = (TextView) this.findViewById(R.id.textDataJarakMaxKendaraan);
        this.lastMbengkel = (TextView) this.findViewById(R.id.textDataLastMbengkel);
        this.activate = (Button) this.findViewById(R.id.kendaraan_button_activate);
        this.activate.setEnabled(false);
        
        this.listKendaraan = (ListView) this.findViewById(R.id.LVK);
        this.listKendaraan.setSelected(true);
        this.listKendaraan.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				SQLiteDatabase db = dbHelper.getReadableDatabase();
				cursor = db.rawQuery("select * from kendaraan", null);
				cursor.moveToPosition(arg2);
				iddata = cursor.getString(0);
				noPol.setText(cursor.getString(2));
				jenisKendaraan.setText(cursor.getString(1));
				jarakMax.setText(Double.parseDouble(cursor.getString(3))/1000+"");
				try{
					SQLiteDatabase jdb = dbHelper.getReadableDatabase();
					Cursor cursor = jdb.rawQuery("SELECT SUM(jarak) FROM perjalanan WHERE id_kendaraan="+iddata, null);
			        if (cursor.moveToFirst()) {
			        	jarakDitempuh.setText(String.format("%.2f", cursor.getDouble(0))+" m");
			        }else{
			        	jarakDitempuh.setText("0 m");
			        }
			        cursor = jdb.rawQuery("SELECT * FROM latest_mbengkel WHERE id_kendaraan = "+iddata, null);
			        if(cursor.moveToFirst()){
			        	lastMbengkel.setText(cursor.getDouble(1)/1000+"@"+cursor.getString(2));
        			}else{
        				lastMbengkel.setText(null);
        			}
					activate.setEnabled(true);
					db.close();
				}catch(Exception e){
				}
			}        	
        });
        dbHelper = new MySQLHelper(this);
        this.activate.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				prefs = getSharedPreferences(prefName, MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("idkendaraan", iddata);
				editor.putString("nopol", noPol.getText().toString());
				editor.commit();
				Toast.makeText(getBaseContext(), "Nomor Polisi : "+noPol.getText()+" dipilih", Toast.LENGTH_LONG).show();
			}
		});
        view();
        
        SharedPreferences prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        Toast.makeText(getBaseContext(), "Nomor polisi sedang aktif "+prefs.getString("nopol", "tidak ada"), Toast.LENGTH_SHORT).show();
	}
	private void view() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try{
			cursor = db.rawQuery("select * from kendaraan", null);
			adapter = new android.support.v4.widget.SimpleCursorAdapter(this, R.layout.isvo_view_kendaraan, cursor, new String[] {"nopol","produk"}, new int[] {R.id.noPol,R.id.jenisKendaraan}, 0);
			listKendaraan.setAdapter(adapter);
			db.close();
		}catch(Exception e){
			Log.d("Exception", e.toString());
		}
	}
	
	private void dialogEdit(int f){
		final Dialog myDialog = new Dialog(this);
		final EditText noPol;
		final EditText jenis;
		final EditText jMax;
		myDialog.setContentView(R.layout.isvo_dialog_edit_kendaraan);
		myDialog.setCancelable(true);
		Button simpan = (Button) myDialog.findViewById(R.id.dialog_kendaraan_simpan_but);
		Button batal = (Button) myDialog.findViewById(R.id.dialog_kendaraan_batal_but);
		noPol = (EditText) myDialog.findViewById(R.id.editNopol);
		jenis = (EditText) myDialog.findViewById(R.id.editMerk);
		jMax = (EditText) myDialog.findViewById(R.id.editJarMax);
		//edit existing data? 
		if(this.noPol.getText().equals("") || f == 0){	
			//if "", add new data
			myDialog.setTitle("Masukkan kendaraan baru");
		}else{
			//if not "", edit that data
			myDialog.setTitle("Ubah kendaraan");
			noPol.setText(this.noPol.getText());
			jenis.setText(this.jenisKendaraan.getText());
			jMax.setText(this.jarakMax.getText());
		}
		myDialog.show();
		simpan.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				String nopol = StringEscapeUtils.escapeJava(noPol.getText().toString());
				String produk = StringEscapeUtils.escapeJava(jenis.getText().toString());
				Double maxlength = Double.parseDouble(StringEscapeUtils.escapeJava(jMax.getText().toString())) * 1000;
				db.execSQL("insert or replace into kendaraan (_id, nopol, produk, maxlength) values ("+iddata+",'"+nopol+"','"+produk+"',"+maxlength+")");
				myDialog.hide();
				iddata = null;
				view();
			}
		});
		batal.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				myDialog.cancel();
				iddata = null;
				view();
			}
		});		
	}
	
	private void deleteData(String np){
		if(!np.equals(null)){
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.execSQL("delete from kendaraan where nopol = '"+np+"'");
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.datalistmanipulationmenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.datalistmodmenu_ubah){
        	dialogEdit(1);
            return true;
        }else if(item.getItemId() == R.id.datalistmodmenu_tambah){
        	dialogEdit(0);
            return true;
        }else if(item.getItemId() == R.id.datalistmodmenu_hapus){
        	deleteData(this.noPol.getText().toString());
        	view();
            return true;
        }else if(item.getItemId() == R.id.datalistmodmenu_batal){
        	this.noPol.setText(null);
        	this.jarakDitempuh.setText(null);
        	this.jarakMax.setText(null);
        	this.jenisKendaraan.setText(null);
        	this.iddata = null;
        	activate.setEnabled(false);
            return true;
        }else{
        	return super.onOptionsItemSelected(item);
        }
    }
}
