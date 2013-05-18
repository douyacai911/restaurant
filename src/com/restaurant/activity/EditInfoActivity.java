package com.restaurant.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.restaurant.util.HttpUtil;

public class EditInfoActivity extends Activity {
	private TheApplication app;
	private EditText epwd,epwd2,eemail,etel;
	private String pwd,pwd2,email,tel;
	private Handler mainHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_info);
		setTitle("�޸ĸ�����Ϣ");
		app = (TheApplication) getApplication(); 
		epwd = (EditText)findViewById(R.id.editText1);
		epwd2 = (EditText)findViewById(R.id.editText2);
		eemail = (EditText)findViewById(R.id.editText3);
		etel = (EditText)findViewById(R.id.editText4);
		
		findViewById(R.id.button1).setOnClickListener(
				new View.OnClickListener() {		//���ע�ᰴť
					@Override
					public void onClick(View view) {
						String pwdtext = epwd.getText().toString();
						String pwdtext2 = epwd2.getText().toString();
						String emailtext = eemail.getText().toString();
						String tel = etel.getText().toString();
						boolean cancel = false;
						View focusView = null;if (TextUtils.isEmpty(pwdtext)) { //����Ϊ��
							epwd.setError(getString(R.string.error_field_required));
							focusView  = epwd;
							cancel = true;
						} else if (pwdtext.length() < 4) { //����С��4λ
							epwd.setError(getString(R.string.error_invalid_password));
							focusView = epwd;
							cancel = true;
						} else if (!pwdtext.equals(pwdtext2)){ //���벻һ��
							epwd2.setError(getString(R.string.error_not_same_password));
							focusView = epwd2;
							cancel = true;
						} else if (TextUtils.isEmpty(emailtext)) { //emailΪ��
							eemail.setError(getString(R.string.error_email_required));
							focusView = eemail;
							cancel = true;
						} else if (!emailtext.contains("@")) { //email��ʽ����ȷ
							eemail.setError(getString(R.string.error_invalid_email));
							focusView = eemail;
							cancel = true;
						} else if (TextUtils.isEmpty(tel)) { //emailΪ��
							etel.setError("�����������ֻ�����");
							focusView = etel;
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
				case 0:
					Toast.makeText(EditInfoActivity.this, "�Բ������Ժ�����",Toast.LENGTH_SHORT).show();
					findViewById(R.id.progressBar1).setVisibility(View.GONE);
					break;
				case 1:
					Toast.makeText(EditInfoActivity.this, "�޸ĳɹ�",Toast.LENGTH_SHORT).show();
					MyMenuActivity.instance.finish();	
					Intent intent = new Intent().setClass(EditInfoActivity.this, MyMenuActivity.class);
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
			String password = epwd.getText().toString();
			String email = eemail.getText().toString();
			String tel = etel.getText().toString();
			String result = goRegister(password,email,tel);
			
			return Integer.parseInt(result);
		}
			
		private String goRegister(String password,String email,String tel){
			// ��ѯ����
			JSONObject json = new JSONObject();
			String param = null;
			try {
				json.put("restid", app.getRestid());
				json.put("password", password);
				json.put("email", email);
				json.put("tel", tel);
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
			String url = HttpUtil.BASE_URL+"EditInfoRServlet?"+registerString;
			// ��ѯ���ؽ��
			return HttpUtil.queryStringForPost(url);
		}
	

}
