package com.restaurant.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.restaurant.util.HttpUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;

public class AddDishActivity extends Activity {
	private int restid = 0;
	private EditText dishname,price,description;
	private int categoryid;
	private Handler mainHandler;
	private static final String[] m={"炒菜","凉菜","主食","酒水","其他"};  
    private Spinner spinner;  
    private ArrayAdapter<String> adapter;  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        // TODO Auto-generated method stub  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_add_dish);  
        Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		restid = bundle.getInt("restid");
        
        dishname = (EditText)findViewById(R.id.EditText1);
		price = (EditText)findViewById(R.id.EditText4);
		description = (EditText)findViewById(R.id.EditText2);
		findViewById(R.id.button1).setOnClickListener(
				new View.OnClickListener() {		//点击注册按钮
					@Override
					public void onClick(View view) {
						String Dishname = dishname.getText().toString();
						String Price = price.getText().toString();
						String Description = description.getText().toString();
						boolean cancel = false;
						View focusView = null;
						if (TextUtils.isEmpty(Dishname)) {// 用户名为空
							dishname.setError("菜品名不能为空");
							focusView = dishname;
							cancel = true;
						}else if (TextUtils.isEmpty(Price)) { //密码为空
							price.setError(getString(R.string.error_field_required));
							focusView  = price;
							cancel = true;
						
						} else if (TextUtils.isEmpty(Description)) { //email为空
							description.setError(getString(R.string.error_email_required));
							focusView = description;
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
					Toast.makeText(AddDishActivity.this, "对不起，此用户名已用于注册",Toast.LENGTH_SHORT).show();
					break;
				default:
					Toast.makeText(AddDishActivity.this, "添加成功",Toast.LENGTH_SHORT).show();
					LoginActivity.instance.finish();	//关闭LoginActivity
					Intent intent = new Intent().setClass(AddDishActivity.this, MyMenuActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("restid", restid);
					intent.putExtras(bundle);
	    			startActivity(intent);
	    			finish();
	    			
					break;
				}
				super.handleMessage(msg);
			}
			
		};
        spinner = (Spinner) findViewById(R.id.CategorySelect);  
        //将可选内容与ArrayAdapter连接起来  
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);  
          
        //设置下拉列表的风格  
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
          
        //将adapter 添加到spinner中  
        spinner.setAdapter(adapter);  
          
        spinner.setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        	categoryid = position;
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        
                    }
                }); 
        
    }  
    Runnable progressThread = new Runnable(){
		@Override
		public void run() {
			Message msg = new Message();
				new Thread();
				 
					msg.what=add();
			
				mainHandler.sendMessage(msg);
			}
		};
		
		private int add(){
//			
			String Dishname = dishname.getText().toString();
			double Price = Double.parseDouble(price.getText().toString());
			String Description = description.getText().toString();
			int Categoryid = categoryid;
			String result = goAdd(restid,Dishname, Price,Description,Categoryid);
		if (result != null && result.equals("-1")) {
			return -1;// 用户名重复
		} 
			return Integer.parseInt(result);
	}
			
		private String goAdd(int restid,String dishname,double price,String description,int categoryid){
			JSONObject json = new JSONObject();
			String param =null;
			try {
				json.put("restid", restid);
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
			// 查询参数
			
			String url = HttpUtil.BASE_URL+"AddDishServlet?"+AddString;
			// 查询返回结果
			return HttpUtil.queryStringForPost(url);
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
		getMenuInflater().inflate(R.menu.add_dish, menu);
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
