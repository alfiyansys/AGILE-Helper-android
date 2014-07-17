package com.yammy.meter;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class About extends Activity {
	WebView browser;
	String data = "" +
			"<head>" +
			"</head>" +
			"<body>" +
			"<h3>ISVO Meter (Ian Sammy Vehicle Oil Meter)</h3>" +
			"Demo Video : <a href=\"www.youtube.com/watch?v=4QIXfow7Oa8\">www.youtube.com/watch?v=4QIXfow7Oa8</a><br>"+
			"see details at : <a href=\"https://github.com/alfiyansys/ISVO-meter#readme\">https://github.com/alfiyansys/ISVO-meter#readme</a>" +
			"</body>";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        setContentView(R.layout.isvo_activity_about);
        browser = (WebView) this.findViewById(R.id.webkit);
        browser.loadData(data, "text/html", "UTF-8");
	}
}
