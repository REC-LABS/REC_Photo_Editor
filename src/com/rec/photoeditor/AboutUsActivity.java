package com.rec.photoeditor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutUsActivity extends Activity implements OnClickListener {

	private ImageView recWebSiteButton;
	private ImageView backButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
		recWebSiteButton = (ImageView) findViewById(R.id.rec_website_button);
		recWebSiteButton.setOnClickListener(this);
		backButton = (ImageView) findViewById(R.id.about_us_back_button);
		backButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.about_us_back_button:
			finish();
			break;
		case R.id.rec_website_button:
			   openRECWebsite();
			break;

		default:
			break;
		}

	}

	private void openRECWebsite() {
		Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( "http://www.rec-global.com" ) );
		    startActivity( browse );
	}
 
	
}
