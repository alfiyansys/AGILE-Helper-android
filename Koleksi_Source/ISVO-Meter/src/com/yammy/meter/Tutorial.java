package com.yammy.meter;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class Tutorial extends Activity {
	WebView wv; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.isvo_activity_tutorial);		 
        wv = (WebView) findViewById(R.id.webTutor);  
        wv.loadUrl("file:///android_asset/tutorial.html");
	}
}
