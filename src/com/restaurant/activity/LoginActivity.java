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

		setTitle("��ӭʹ�ò�������ƽ̨--��Ұ�");
		app = (TheApplication) getApplication(); 
		userText = (EditText)findViewById(R.id.userText);
		pwdText = (EditText)findViewById(R.id.pwdText);
		app = (TheApplication) getApplication(); 
		instance = this; //ָ���ر���
		 
		
		
		
		findViewById(R.id.LoginBtn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						String usertext = userText.getText().toString();
						String pwdtext = pwdText.getText().toString();
						boolean cancel = false;
						View focusView = null;
						if (TextUtils.isEmpty(usertext)) { //�û���Ϊ��
							userText.setError(getString(R.string.error_username_required));
							focusView  = userText;
							cancel = true;
						}else if (TextUtils.isEmpty(pwdtext)){ //����Ϊ��
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
						
						//��ת��ע��ҳ��
						
					}
				});
		this.mainHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				findViewById(R.id.progressBar1).setVisibility(View.GONE);
				switch(msg.what){
				case -3:
					Toast.makeText(LoginActivity.this, "�������",Toast.LENGTH_SHORT).show();
					break;
				case -2:
					Toast.makeText(LoginActivity.this, "�������",Toast.LENGTH_SHORT).show();
					break;
				case -1:
					Toast.makeText(LoginActivity.this, "�޴��˻�",Toast.LENGTH_SHORT).show();
					break;
				default:
					Toast.makeText(LoginActivity.this, "��½�ɹ�",Toast.LENGTH_SHORT).show();
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




	//��¼����
	private int login(){
//		
		// ����û�����
		String username = userText.getText().toString();
		// �������
		String password = pwdText.getText().toString();
		// ��õ�¼���
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
		// ��ѯ����
		// URL
		String url = HttpUtil.BASE_URL+"LoginServlet?"+queryString;
		// ��ѯ���ؽ��
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
