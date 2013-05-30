package com.restaurant.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.restaurant.util.HttpUtil;
import com.restaurant.activity.LoginActivity;
import com.restaurant.activity.RegisterActivity;
import com.restaurant.activity.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	public static LoginActivity instance = null;
	private EditText userText,pwdText;
	private Handler mainHandler;

	private TheApplication app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		setTitle("欢迎使用餐饮服务平台--店家版");
		app = (TheApplication) getApplication(); 
		userText = (EditText)findViewById(R.id.userText);
		pwdText = (EditText)findViewById(R.id.pwdText);
		app = (TheApplication) getApplication(); 
		instance = this; //指定关闭用
		 
		
		
		
		findViewById(R.id.LoginBtn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						String usertext = userText.getText().toString();
						String pwdtext = pwdText.getText().toString();
						boolean cancel = false;
						View focusView = null;
						if (TextUtils.isEmpty(usertext)) { //用户名为空
							userText.setError(getString(R.string.error_username_required));
							focusView  = userText;
							cancel = true;
						}else if (TextUtils.isEmpty(pwdtext)){ //密码为空
							pwdText.setError(getString(R.string.error_field_required));
							focusView = pwdText;
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
		
		findViewById(R.id.button2).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
						startActivity(intent);
						
						//跳转到注册页面
						
					}
				});
		this.mainHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				findViewById(R.id.progressBar1).setVisibility(View.GONE);
				switch(msg.what){
				case -3:
					Toast.makeText(LoginActivity.this, "网络错误",Toast.LENGTH_SHORT).show();
					break;
				case -2:
					Toast.makeText(LoginActivity.this, "密码错误",Toast.LENGTH_SHORT).show();
					break;
				case -1:
					Toast.makeText(LoginActivity.this, "无此账户",Toast.LENGTH_SHORT).show();
					break;
				default:
					Toast.makeText(LoginActivity.this, "登陆成功",Toast.LENGTH_SHORT).show();
					Intent intent = new Intent().setClass(LoginActivity.this, MyMenuActivity.class);
					app.setRestid(msg.what);
//					Bundle bundle = new Bundle();
//					bundle.putInt("restid", msg.what);
//					intent.putExtras(bundle);
	    			startActivity(intent);
	    			finish();
					break;
				}
				super.handleMessage(msg);
			}
			
		};
	}




	//登录方法
	private int login(){
//		
		// 获得用户名称
		String username = userText.getText().toString();
		// 获得密码
		String password = pwdText.getText().toString();
		// 获得登录结果
		String result=query(username,password);
		String[] msgs = result.split(";");
		String[] flag = msgs[0].split("=");
		
		
		if (flag[0] != null && flag[0].equals("restid")) {
			int restid = Integer.parseInt(flag[1]);
			return restid;
		} else if (result != null && result.equals("0")) {
			return -1;
		} else if (result != null && result.equals("1")) {
			return -2;
		} else
			return -3;
	}
	private String query(String username,String password){
		String param = null;
		JSONObject json = new JSONObject();
		try {
			json.put("username", username);
			json.put("password", password);
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
		String queryString = "param="+param;
		// 查询参数
		// URL
		String url = HttpUtil.BASE_URL+"LoginServlet?"+queryString;
		// 查询返回结果
		return HttpUtil.queryStringForPost(url);
	}

	Runnable progressThread = new Runnable(){
		@Override
		public void run() {
			Message msg = new Message();
				new Thread();
				 
					msg.what=login();
			
				mainHandler.sendMessage(msg);
			}
		};
	

	

}
