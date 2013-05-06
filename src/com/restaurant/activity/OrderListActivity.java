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
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class OrderListActivity extends Activity {

	public static OrderListActivity instance = null;
	private int restid = 0;
	private TextView textview;
	private Handler mainHandler;
	private SimpleAdapter listItemAdapter;
	private ListView list;
	private TheApplication app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_list);

		instance = this; //ָ���ر���
		app = (TheApplication) getApplication(); 
		restid = app.getRestid();
		
		list = (ListView) findViewById(R.id.listView1);
		setTitle("����һ��");
		new Thread(progressThread).start();
		
		// ��ӵ��
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				HashMap<String, Object> thisorder = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
				int thisorderid = (Integer) thisorder.get("orderid");
				boolean flag = (Boolean) thisorder.get("completeflag");
				Intent intent = new Intent().setClass(OrderListActivity.this, OrderDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("orderid", thisorderid);
				bundle.putBoolean("completeflag", flag);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		
		
		
		
		this.mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// findViewById(R.id.progressBar1).setVisibility(View.GONE);
				if(msg.what==-123){
					Toast.makeText(OrderListActivity.this, "���޶���",Toast.LENGTH_SHORT).show();
				}else{
				listItemAdapter = new SimpleAdapter(OrderListActivity.this,
						(ArrayList<HashMap<String, Object>>) msg.obj,// ����Դ
						R.layout.order_list_layout,// ListItem��XMLʵ��
						// ��̬������ImageItem��Ӧ������
						new String[] { "eattime", "maketime","delivery","flagString" },
						// ImageItem��XML�ļ������һ��ImageView,����TextView ID
						new int[] { R.id.textView4, R.id.textView2, R.id.textView5,R.id.textView6}

				);

				// ��Ӳ�����ʾ
				list.setAdapter(listItemAdapter);
				}

				super.handleMessage(msg);
			}

		};
	}

	
	
	
	
	
	Runnable progressThread = new Runnable() {
		@Override
		public void run() {
			Message msg = new Message();
			new Thread();
			Object flag = jsonToOrderList(restid);
			if(flag==null){
				msg.what = -123;
			}else{
				msg.obj = flag;
			}
			mainHandler.sendMessage(msg);
		}
	};
	
	private String goSearch(int id) {
		// ��ѯ����

		String registerString = "restid=" + id;
		// URL
		String url = HttpUtil.BASE_URL + "OrderListServlet?" + registerString;
		// ��ѯ���ؽ��
		return HttpUtil.queryStringForPost(url);
	}

	private ArrayList<HashMap<String, Object>> jsonToOrderList(int id) {
		String jsonString = goSearch(id);
		if(jsonString.equals("-1")){
			return null;
		}
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		try {
			JSONObject json = new JSONObject(jsonString);
			JSONArray jsonArray = json.getJSONArray("order");

			for (int i = 0; i < jsonArray.length(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				JSONObject order = (JSONObject) jsonArray.get(i);
				int orderid = order.getInt("orderid");
				boolean delivery = order.getBoolean("delivery");
				boolean completeflag = order.getBoolean("completeflag");
				String eattime = order.getString("eattime");
				String maketime = order.getString("maketime");
				String theDelivery = "";
				String flagString = "";
				
				if(delivery){
					theDelivery = "����";
				}else{
					theDelivery = "����";
				}
				if(completeflag){
					flagString = "�����";
				}else{
					flagString = " ";
				}
				map.put("orderid", orderid);
				map.put("delivery", theDelivery);
				map.put("eattime", eattime);
				map.put("maketime", maketime);
				map.put("completeflag", completeflag);
				map.put("flagString", flagString);
				map.put("index", i);
				listItem.add(map);
			}
			return listItem;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.order_list, menu);
		return true;
	}

}
