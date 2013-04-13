package com.restaurant.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MyMenuActivity extends Activity {
	private int restid=0;
	private TextView textview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_menu);
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		restid = bundle.getInt("restid");
//		Toast.makeText(MyMenuActivity.this,
//				Integer.toString(restid), Toast.LENGTH_SHORT)
//				.show();
		
		findViewById(R.id.Button1).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent().setClass(MyMenuActivity.this, AddDishActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("restid", restid);
						intent.putExtras(bundle);
		    			startActivity(intent);
						
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_menu, menu);
		return true;
	}

}
