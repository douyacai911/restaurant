package com.restaurant.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.restaurant.activity.R;
import com.restaurant.util.HttpUtil;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class BeginActivity extends Activity {
	private EditText restname, tel, address;
	private TextView show;
	private Handler mainHandler;
	private String coord = null;
    private RadioGroup radiogroup;  
    private RadioButton radio1,radio2;  
    private int delivery = 0;
    private int restid = 0;
    private TheApplication app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_begin);
		app = (TheApplication) getApplication(); 
//		Intent intent = this.getIntent();
//		Bundle bundle = intent.getExtras();
//		restid = bundle.getInt("restid");
		restid = app.getRestid();
		restname = (EditText) findViewById(R.id.EditText1);
		tel = (EditText) findViewById(R.id.EditText4);
		address = (EditText) findViewById(R.id.EditText2);
        radiogroup=(RadioGroup)findViewById(R.id.radiogroup1);  
		radio1 = (RadioButton) findViewById(R.id.radioButton1);
		radio2 = (RadioButton) findViewById(R.id.radioButton2);
		// 监听是否外卖选项
		radiogroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (checkedId == radio2.getId()) {
							delivery = 0;
						} else {
							delivery = 1;
						}
					}
				});

		findViewById(R.id.Button1).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						
						String Restname = restname.getText().toString();
						String Tel = tel.getText().toString();
						String Address = address.getText().toString();
						boolean cancel = false;
						View focusView = null;
						if (TextUtils.isEmpty(Restname)) {// 餐厅名为空
							restname.setError(getString(R.string.error_username_required));
							focusView = restname;
							cancel = true;
						}else if (TextUtils.isEmpty(Tel)) { //手机号为空
							tel.setError(getString(R.string.error_field_required));//改正错误信息
							focusView  = tel;
							cancel = true;
						}  else if (TextUtils.isEmpty(Address)) { //地址为空
							address.setError(getString(R.string.error_email_required));
							focusView = address;
							cancel = true;
						} 
						
						if (cancel) {
							focusView.requestFocus();
						} else {
							
							coord = performGeocode(address.getText().toString());
							if (coord != null & coord.equals("-1")) { // 未查到对应地理坐标
								Toast.makeText(BeginActivity.this,
										"该地址用于地图显示，请详细输入", Toast.LENGTH_SHORT)
										.show();
							} else {
								findViewById(R.id.progressBar1).setVisibility(0);
								new Thread(progressThread).start();
							}
						}
					}
				});
		this.mainHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				findViewById(R.id.progressBar1).setVisibility(View.GONE);
				switch(msg.what){
				case -1:
					Toast.makeText(BeginActivity.this, "对不起，此餐厅名已用于注册",Toast.LENGTH_SHORT).show();
					break;//TODO 异常
				default:
					Toast.makeText(BeginActivity.this, "注册成功",Toast.LENGTH_SHORT).show();
//					LoginActivity.instance.finish();	//关闭LoginActivity
					Intent intent = new Intent().setClass(BeginActivity.this, MyMenuActivity.class);
//					Bundle bundle = new Bundle();
//					bundle.putInt("restid", msg.what);
//					intent.putExtras(bundle);
					app.setRestid(msg.what);
	    			startActivity(intent);
	    			finish();
	    			
					break;
				}
				super.handleMessage(msg);
			}
			
		};
	}

	Runnable progressThread = new Runnable(){
		@Override
		public void run() {
			Message msg = new Message();
				new Thread();
				 
					msg.what=restRegister(coord);
			
				mainHandler.sendMessage(msg);
			}
		};
		
		private int restRegister(String coord){
//			
			int Restid = this.restid;
			String Restname = restname.getText().toString();
			String Tel = tel.getText().toString();
			String Address = address.getText().toString();
			String Location = coord;
			int Delivery = this.delivery;
			String result = goRestRegister(Restid,Restname,Tel,Address,Location,Delivery);
			
			return Integer.parseInt(result);
	}
			
		private String goRestRegister(int Restid,String Restname,String Tel,String Address,String Location,int Delivery){
			// 查询参数
			JSONObject json = new JSONObject();
			String param = null;
			try {
				json.put("restid", Restid);
				json.put("restname", Restname);
				json.put("tel", Tel);
				json.put("address", Address);
				json.put("location", Location);
				json.put("delivery", Delivery);
				
				
				param = json.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				param = URLEncoder.encode(param, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String registerString = "param="+param;
			// URL
			String url = HttpUtil.BASE_URL+"RestRegisterServlet?"+registerString;
			// 查询返回结果
			return HttpUtil.queryStringForPost(url);
		}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.begin, menu);
		return true;
	}

	private String performGeocode(String add) {
		String result = null;
			Geocoder geocoder = new Geocoder(this, Locale.CHINA);
			try {
				List<Address> addresses = geocoder.getFromLocationName(add, 1);
				if (addresses != null && addresses.size() != 0) {
					result = Double.toString(addresses.get(0).getLongitude())
							+ ","
							+ Double.toString(addresses.get(0).getLatitude());
				} else {
					result = "-1";
				}
			} catch (IOException e) {
				Toast.makeText(BeginActivity.this, e.getMessage(),
						Toast.LENGTH_SHORT).show();
				result = "0";
			}

		

		return result;
	}

	protected boolean isRouteDisplayed() {
		return false;
	}
}
