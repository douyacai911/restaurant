package com.restaurant.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.restaurant.util.HttpUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.support.v4.app.NavUtils;

public class EditDishActivity extends Activity {
	private int foodid = 0;
	private EditText edishname,eprice,edescription;
	private int categoryid;
	private double price;
	private Handler mainHandler,mainHandler2;
	private static final String[] m={"����","����","��ʳ","��ˮ","����"};  
    private Spinner spinner;  
    private ArrayAdapter<String> adapter;  
    private String dishname,description;

	private ProgressBar progressbar;
	private TheApplication app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_dish);
		// Show the Up button in the action bar.
		setupActionBar();
		app = (TheApplication) getApplication(); 
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		foodid = bundle.getInt("foodid");
		dishname = bundle.getString("dishname");
		price = bundle.getDouble("price");
		categoryid = bundle.getInt("categoryid");
		description = bundle.getString("description");
		
		edishname = (EditText)findViewById(R.id.EditText1);
		eprice = (EditText)findViewById(R.id.EditText4);
		edescription = (EditText)findViewById(R.id.EditText2);
		
		edishname.setText(dishname);
		eprice.setText(String.valueOf(price));
		edescription.setText(description);
		

		progressbar = (ProgressBar) findViewById(R.id.progressBar1);
		
		spinner = (Spinner) findViewById(R.id.CategorySelect);  
        //����ѡ������ArrayAdapter��������  
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);  
          
        //���������б�ķ��  
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
          
        //��adapter ��ӵ�spinner��  
        spinner.setAdapter(adapter);  
        spinner.setSelection(categoryid-1, true);
        spinner.setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        	categoryid = position;
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        
                    }
                }); 
        
        findViewById(R.id.button1).setOnClickListener(
				new View.OnClickListener() {		//����޸İ�ť
					@Override
					public void onClick(View view) {
						String Dishname = edishname.getText().toString();
						String Price = eprice.getText().toString();
						String Description = edescription.getText().toString();
						boolean cancel = false;
						View focusView = null;
						if (TextUtils.isEmpty(Dishname)) {
							edishname.setError("��Ʒ������Ϊ��");
							focusView = edishname;
							cancel = true;
						}else if (TextUtils.isEmpty(Price)) {
							eprice.setError("���۲���Ϊ��");
							focusView  = eprice;
							cancel = true;
						
						} else if (TextUtils.isEmpty(Description)) { 
							edescription.setError("��дһЩ����");
							focusView = edescription;
							cancel = true;
						} 

						if (cancel) {
							focusView.requestFocus();
						} else {
							progressbar.setVisibility(0);

							new Thread(progressThread).start();
						}
					}
				});
        
        findViewById(R.id.button2).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						
							final AlertDialog.Builder builder = new Builder(
									EditDishActivity.this);
							builder.setTitle("��Ʒɾ��")
									.setMessage("��ǰ��Ʒ�������Ĳ˵���ɾ����ȷ�������������ȷ����")
									.setPositiveButton(
											"ȷ��",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialoginterface,
														int i) {
													// ��ť�¼�
													Toast.makeText(
															EditDishActivity.this,
															"���Ժ�",
															Toast.LENGTH_SHORT)
															.show();
													
													try {
														progressbar.setVisibility(0);
														new Thread(
																progressThread2)
																.start();
													} catch (Exception e) {
														Toast.makeText(
																EditDishActivity.this,
																"Failed to send SMS",
																Toast.LENGTH_LONG)
																.show();
														e.printStackTrace();
													}

												}
											})
									.setNegativeButton(
											"����",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
												}
											}).show();
						}
					}
				);
        findViewById(R.id.button3).setOnClickListener(
				new View.OnClickListener() {		//����鿴���۰�ť
					@Override
					public void onClick(View view) {
						Intent intent = new Intent().setClass(EditDishActivity.this, ReviewActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("foodid", foodid);
						bundle.putString("dishname", dishname);
						intent.putExtras(bundle);
		    			startActivity(intent);
						}
					}
				);
        this.mainHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				progressbar.setVisibility(View.GONE);
				switch(msg.what){
				case -1:
					Toast.makeText(EditDishActivity.this, "�Բ������Ĳ˵�������ʹ�ô˲����Ĳ�Ʒ",Toast.LENGTH_SHORT).show();
					break;
				default:
					MyMenuActivity.instance.finish();
					Toast.makeText(EditDishActivity.this, "�޸ĳɹ�",Toast.LENGTH_SHORT).show();
					Intent intent = new Intent().setClass(EditDishActivity.this, MyMenuActivity.class);
//					Bundle bundle = new Bundle();
//					bundle.putInt("restid", restid);
//					intent.putExtras(bundle);
	    			startActivity(intent);
	    			finish();
	    			
					break;
				}
				super.handleMessage(msg);
			}
			
		};
		 this.mainHandler2 = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					progressbar.setVisibility(View.GONE);
					switch(msg.what){
					case 0:
						Toast.makeText(EditDishActivity.this, "�Բ������Ժ�����",Toast.LENGTH_SHORT).show();
						break;
					case 1:
						Toast.makeText(EditDishActivity.this, "ɾ���ɹ�",Toast.LENGTH_SHORT).show();
						MyMenuActivity.instance.finish();
						Intent intent = new Intent().setClass(EditDishActivity.this, MyMenuActivity.class);
//						Bundle bundle = new Bundle();
//						bundle.putInt("restid", restid);
//						intent.putExtras(bundle);
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
				 
					msg.what=edit();
			
				mainHandler.sendMessage(msg);
			}
		};
	Runnable progressThread2 = new Runnable(){
		@Override
		public void run() {
			Message msg = new Message();
				new Thread();
				 
					msg.what=delete();
			
				mainHandler2.sendMessage(msg);
			}
		};
		
		private int edit(){
//			
			String Dishname = edishname.getText().toString();
			double Price = Double.parseDouble(eprice.getText().toString());
			String Description = edescription.getText().toString();
			int Categoryid = categoryid;
			String result = goAdd(foodid,Dishname, Price,Description,Categoryid);
		if (result != null && result.equals("-1")) {
			return -1;// �û����ظ�
		} 
			return Integer.parseInt(result);
	}
			
		private String goAdd(int foodid,String dishname,double price,String description,int categoryid){
			JSONObject json = new JSONObject();
			String param =null;
			try {
				json.put("foodid", foodid);
				json.put("dishname", dishname);
				json.put("price", price);
				json.put("description", description);
				json.put("categoryid", categoryid+1);
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
			String AddString = "param="+param;
			// ��ѯ����
			
			String url = HttpUtil.BASE_URL+"EditDishServlet?"+AddString;
			// ��ѯ���ؽ��
			return HttpUtil.queryStringForPost(url);
		}
		
		private int delete(){
			String deleteString = "foodid="+foodid;
			String url = HttpUtil.BASE_URL+"DeleteDishServlet?"+deleteString;
			return Integer.parseInt(HttpUtil.queryStringForPost(url));
		}
		
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_dish, menu);
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
