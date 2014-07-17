package com.yammy.meter.bengkel;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

public class DataBengkel {
	List<Bengkel> data = new ArrayList<Bengkel>();
	
	public DataBengkel(){
		//Ian, workshop part 1, 12:33 14/06/2014
		data.add(new Bengkel("Harry Service","Raya Pulungan 5, Sedati, Sidoarjo","-7.395173","112.770889"));
		data.add(new Bengkel("-","Raya Buncitan 93, Sedati, Sidoarjo","-7.395600","112.778301"));
		data.add(new Bengkel("Perlindungan Motor","Raya Buncitan 159, Sedati, Sidoarjo","-7.396020","112.782139"));
		data.add(new Bengkel("-","Raya Buncitan, Sedati, Sidoarjo (Dekat SMPN 2 Sedati)","-7.396405","112.787304"));
		data.add(new Bengkel("-","Raya Buncitan, Sedati, Sidoarjo (Dekat SMPN 2 Sedati)","-7.396395","112.787580"));
		data.add(new Bengkel("-","Raya Buncitan, Sedati, Sidoarjo (Dekat SMPN 2 Sedati)","-7.396442","112.787815"));
		data.add(new Bengkel("Mutiara Motor","Depan Akademi Perikanan Sidoarjo","-7.396949","112.790215"));
		data.add(new Bengkel("-","Antara APS- Pasar Kalanganyar","-7.397277","112.791906"));
		data.add(new Bengkel("-","Depan Pasar Kalanganyar, Sedati, Sidoarjo","-7.397405","112.792587"));
		data.add(new Bengkel("-","Raya Kalanganyar 21, Sedati, Sidoarjo","-7.398038","112.795754"));
		data.add(new Bengkel("-","Barat Jembatan Cemandi","-7.401945","112.804827"));
		data.add(new Bengkel("-","Depan Tambak H. Chulaim","-7.399582","112.800713"));
		data.add(new Bengkel("-","Raya Kalanganyar 13, Sedati, Sidoarjo","-7.397829","112.794558"));
		data.add(new Bengkel("Udin Motor","Raya Buncitan 90, Sedati, Sidoarjo","-7.395793","112.779619"));
		data.add(new Bengkel("Yanto Motor","Raya Buncitan 72, Sedati, Sidoarjo","-7.395721","112.778299"));
		data.add(new Bengkel("Rizqi Motor","Raya Buncitan 62, Sedati, Sidoarjo","-7.395674","112.777635"));
		data.add(new Bengkel("Federal Oil","Raya Buncitan 44, Sedati, Sidoarjo","-7.395657","112.776884"));
		data.add(new Bengkel("SAE Motor","Raya Buncitan 14, Sedati, Sidoarjo","-7.395540","112.775874"));
		data.add(new Bengkel("Arta Motor","Raya Buncitan, Pojok Barat, Sedati, Sidoarjo","-7.395369","112.774202"));

		//Sammy, 20:40 14/06/2014
		data.add(new Bengkel("Bengkel Motor 52","Jl Gebang Putih 52, Surabaya","-7.282886","112.785592"));
		data.add(new Bengkel("Mitra Motor4","Jl Gebang Lor 16, Surabaya","-7.281621","112.786555"));
		data.add(new Bengkel("Castrol Bengkel Motor","Jl Kejawan Putih Tambak 20, Surabaya","-7.280432","112.802476"));
		data.add(new Bengkel("Indie Motor","Jl Arif Rahman Hakim 57B, Surabaya","-7.289951","112.798886"));
		data.add(new Bengkel("Wawa Motor","Jl KH Ahmad Dahlan 47, Surabaya","-7.289386","112.800179"));
		data.add(new Bengkel("Ahass Putra Merdeka","Jl KH Ahmad Dahlan 50, Surabaya","-7.289131","112.800286"));

		//Madiun, 12.00 25/06/2014
		data.add(new Bengkel("Family Jaya","Jl Mayjend Sungkono 11, Madiun","-7.639001","111.514761"));
		data.add(new Bengkel("Indro Hero Sakti Motor","Jl Panglima Sudirman 36, Madiun","-7.631096","111.523234"));
		data.add(new Bengkel("Jawa Hinda Ahass","Jl HOS Cokroaminoto 15, Madiun","7.6348849","111.5195414"));
		data.add(new Bengkel("KiuKiu Motor","Jl Trunojoyo 144, Madiun","-7.643383","111.5178641"));
		data.add(new Bengkel("Perkasa Bengkel","Jl Kemiri14, Madiun","-7.6357077","111.5236305"));
		data.add(new Bengkel("Reski Motor","Jl Raya Barat, Madiun","-7.5608751","111.4495722"));
		data.add(new Bengkel("Surya Mustika Motor","Jl P Diponegoro 50-52, Madiun","-7.6237152","111.5300453"));
		data.add(new Bengkel("Perkasa Bengkel","Jl Kemiri14, Madiun","-7.6357077","111.5236305"));
	}
	
	public void executeDB(SQLiteDatabase db){
		String sql;
		for(Bengkel element: this.data){
			sql = "insert into bengkel(nama,alamat,lat,long) values('"+element.nama+"','"+element.alamat+"','"+element.lat+"','"+element.lng+"')";
			db.execSQL(sql);
		}
	}
}

class Bengkel{
	String nama;
	String alamat;
	String lat;
	String lng;
	public Bengkel(String nama, String alamat, String lat, String lng){
		this.nama = nama;
		this.alamat = alamat;
		this.lat = lat;
		this.lng = lng;
	}
}