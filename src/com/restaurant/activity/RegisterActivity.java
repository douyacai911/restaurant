package com.restaurant.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.restaurant.activity.BeginActivity;
import com.restaurant.activity.LoginActivity;
import com.restaurant.activity.RegisterActivity;
import com.restaurant.util.HttpUtil;
import com.restaurant.activity.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class RegisterActivity extends Activity {
	private EditText userText,pwdText,pwdText2,emailText;
	private Handler mainHandler;
	private TheApplication app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		// Show the Up button in the action bar.
		setupActionBar();
		app = (TheApplication) getApplication(); 
		userText = (EditText)findViewById(R.id.EditText1);
		pwdText = (EditText)findViewById(R.id.EditText4);
		pwdText2 = (EditText)findViewById(R.id.EditText2);
		emailText = (EditText)findViewById(R.id.EditText3);
		
		findViewById(R.id.LoginBtn).setOnClickListener(
				new View.OnClickListener() {		//点击注册按钮
					@Override
					public void onClick(View view) {
						String pwdtext = pwdText.getText().toString();
						String pwdtext2 = pwdText2.getText().toString();
						String usertext = userText.getText().toString();
						String emailtext = emailText.getText().toString();
						boolean cancel = false;
						View focusView = null;
						if (TextUtils.isEmpty(usertext)) {// 用户名为空
							userText.setError(getString(R.string.error_username_required));
							focusView = userText;
							cancel = true;
						}else if (TextUtils.isEmpty(pwdtext)) { //密码为空
							pwdText.setError(getString(R.string.error_field_required));
							focusView  = pwdText;
							cancel = true;
						} else if (pwdtext.length() < 4) { //密码小于4位
							pwdText.setError(getString(R.string.error_invalid_password));
							focusView = pwdText;
							cancel = true;
						} else if (!pwdtext.equals(pwdtext2)){ //密码不一致
							pwdText2.setError(getString(R.string.error_not_same_password));
							focusView = pwdText2;
							cancel = true;
						} else if (TextUtils.isEmpty(emailtext)) { //email为空
							emailText.setError(getString(R.string.error_email_required));
							focusView = emailText;
							cancel = true;
						} else if (!emailtext.contains("@")) { //email格式不正确
							emailText.setError(getString(R.string.error_invalid_email));
							focusView = emailText;
							cancel = true;
						}

						if (cancel) {
							focusView.requestFocus();
						} else {
							findViewById(R.id.progressBar1).setVisibility(0);

							new Thread(progressThread).start();
						}
					}
				});
		this.mainHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				findViewById(R.id.progressBar1).setVisibility(View.GONE);
				switch(msg.what){
				case -1:
					Toast.makeText(RegisterActivity.this, "对不起，此用户名已用于注册",Toast.LENGTH_SHORT).show();
					break;
				default:
					Toast.makeText(RegisterActivity.this, "注册成功",Toast.LENGTH_SHORT).show();
					LoginActivity.instance.finish();	//关闭LoginActivity
					Intent intent = new Intent().setClass(RegisterActivity.this, BeginActivity.class);
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
				 
					msg.what=register();
			
				mainHandler.sendMessage(msg);
			}
		};
		
		private int register(){
//			
			// 获得用户名称
			String username = userText.getText().toString();
			// 获得密码
			String password = pwdText.getText().toString();
			// 获得email
			String email = emailText.getText().toString();
			String result = goRegister(username, password,email);
		if (result != null && result.equals("-1")) {
			return -1;// 用户名重复
		} 
			return Integer.parseInt(result);
	}
			
		private String goRegister(String username,String password,String email){
			// 查询参数
			JSONObject json = new JSONObject();
			String param = null;
			try {
				json.put("username", username);
				json.put("password", password);
				json.put("email", email);
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
			String url = HttpUtil.BASE_URL+"RegisterServlet?"+registerString;
			// 查询返回结果
			return HttpUtil.queryStringForPost(url);
		}


	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
