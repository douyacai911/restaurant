package com.restaurant.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
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
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class OrderDetailActivity extends Activity {
	private int restid = 0;
	private int orderid = 0;
	private TextView textview,lastinfo,lastinfo2,ttotal,teattime,tmaketime,tremark,tremark2,ttel;
	private Handler mainHandler,mainHandler2;
	private SimpleAdapter listItemAdapter;
	private ListView list;
	private TheApplication app;
	private boolean completeflag = false;
	private Button button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		
		app = (TheApplication) getApplication(); 
		restid = app.getRestid();
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		orderid = bundle.getInt("orderid");
		completeflag = bundle.getBoolean("completeflag");
		button = (Button) findViewById(R.id.button1);
		if(completeflag){
			button.setEnabled(false);
			button.setText("该订单已被标记为完成");
		}
		list = (ListView) findViewById(R.id.listView1);
		lastinfo = (TextView) findViewById(R.id.textView6);
		lastinfo2 = (TextView) findViewById(R.id.textView9); //就餐人数或送餐地址
		ttotal = (TextView) findViewById(R.id.textView2);
		teattime = (TextView) findViewById(R.id.textView8);
		tmaketime = (TextView) findViewById(R.id.textView7);
		tremark = (TextView) findViewById(R.id.textView10);
		tremark2 = (TextView) findViewById(R.id.textView11);
		ttel = (TextView)findViewById(R.id.textView13);
		setTitle("订单详情");
		new Thread(progressThread).start();
		
		
		
		this.mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// findViewById(R.id.progressBar1).setVisibility(View.GONE);
				if(msg.what==-123){
					Toast.makeText(OrderDetailActivity.this, "对不起，请稍后再试",Toast.LENGTH_SHORT).show();
				}else{
				ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
				JSONObject json = new JSONObject();
				json = (JSONObject) msg.obj;
				try {
					String address,numofpeo;
					boolean delivery = json.getBoolean("delivery");
					String eattime = json.getString("eattime");
					String maketime = json.getString("maketime");
					String total = json.getString("total");
					String customername = json.getString("customername");
					String customertel = json.getString("customertel");
					setTitle("来自："+customername+"的订单");
					findViewById(R.id.textView12).setVisibility(0);
					ttel.setText(customertel);
					ttel.setVisibility(0);
					if(json.has("remark")){
						tremark2.setText(json.getString("remark"));
						tremark.setVisibility(0);
						tremark2.setVisibility(0);
					}
					ttotal.setText(total);
					teattime.setText(eattime);
					tmaketime.setText(maketime);
					if(delivery){
						lastinfo.setText("送餐地址：");
						address = json.getString("address");
						lastinfo2.setText(address);
						lastinfo.setVisibility(0);
						lastinfo2.setVisibility(0);
						
					}else{
						lastinfo.setText("就餐人数：");
						numofpeo = json.getString("numofpeo");
						lastinfo2.setText(numofpeo);
						lastinfo.setVisibility(0);
						lastinfo2.setVisibility(0);
					}
					JSONArray jsonArray = json.getJSONArray("orderdetailarray");
					for (int i = 0; i < jsonArray.length(); i++) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						JSONObject orderdetail = (JSONObject) jsonArray.get(i);
						String dishname = orderdetail.getString("dishname");
						String quantity = orderdetail.getString("quantity");
						String subtotal = orderdetail.getString("subtotal");
						
						
						map.put("dishname", dishname);
						map.put("quantity", quantity);
						map.put("subtotal", subtotal);
						map.put("index", i);
						listItem.add(map);
				}} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
				listItemAdapter = new SimpleAdapter(OrderDetailActivity.this,listItem,// 数据源
						R.layout.orderdetail_list_layout,// ListItem的XML实现
						// 动态数组与ImageItem对应的子项
						new String[] { "dishname", "quantity","subtotal" },
						// ImageItem的XML文件里面的一个ImageView,两个TextView ID
						new int[] { R.id.textView1, R.id.textView4, R.id.textView3}

				);

				// 添加并且显示
				list.setAdapter(listItemAdapter);
				}

				super.handleMessage(msg);
			}

			};
			
			button.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							
								final AlertDialog.Builder builder = new Builder(
										OrderDetailActivity.this);
								builder.setTitle("添加订单完成标记")
										.setMessage("当前订单将被标记为完成，顾客将可以对此添加评价，确认无误后请点击“确定”")
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialoginterface,
															int i) {
														// 按钮事件
														Toast.makeText(
																OrderDetailActivity.this,
																"请稍候",
																Toast.LENGTH_SHORT)
																.show();
														
														try {
															new Thread(
																	progressThread2)
																	.start();
														} catch (Exception e) {
															Toast.makeText(
																	OrderDetailActivity.this,
																	"Failed to send SMS",
																	Toast.LENGTH_LONG)
																	.show();
															e.printStackTrace();
														}

													}
												})
										.setNegativeButton(
												"返回",
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
			this.mainHandler2 = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// findViewById(R.id.progressBar1).setVisibility(View.GONE);
					if(msg.what==1){
						OrderListActivity.instance.finish();
						Toast.makeText(OrderDetailActivity.this,"完成标记添加成功",
								Toast.LENGTH_LONG)
								.show();
						Intent intent = new Intent().setClass(OrderDetailActivity.this, OrderListActivity.class);
						startActivity(intent);
						finish();
					}else{
						Toast.makeText(OrderDetailActivity.this,"对不起，请稍后再试",
								Toast.LENGTH_LONG)
								.show();
					}

					}};
	}

	
	Runnable progressThread = new Runnable() {
		@Override
		public void run() {
			Message msg = new Message();
			new Thread();
			JSONObject flag = getOrderDetailJson(orderid);
			if(flag==null){
				msg.what = -123;
			}else{
				msg.obj = flag;
			}
			mainHandler.sendMessage(msg);
		}
	};
	Runnable progressThread2 = new Runnable() {
		@Override
		public void run() {
			Message msg = new Message();
			new Thread();
			String result = setCompleteFlag(orderid);
			if(Integer.parseInt(result)==1){
				msg.what = 1;
			}else{
				msg.what = 0;
			}
			mainHandler2.sendMessage(msg);
		}
	};
	
	private String goSearch(int id) {
		// 查询参数

		String registerString = "orderid=" + id;
		// URL
		String url = HttpUtil.BASE_URL + "OrderDetailServlet?" + registerString;
		// 查询返回结果
		return HttpUtil.queryStringForPost(url);
	}
	private JSONObject getOrderDetailJson(int id) {
		String jsonString = goSearch(id);
		if(jsonString.equals("-1")){
			return null;
		}
		try {
			JSONObject json = new JSONObject(jsonString);
			return json;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	private String setCompleteFlag(int id) {
		// 查询参数

		String registerString = "orderid=" + id;
		// URL
		String url = HttpUtil.BASE_URL + "SetCompleteServlet?" + registerString;
		// 查询返回结果
		return HttpUtil.queryStringForPost(url);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.order_detail, menu);
		return true;
	}

}
