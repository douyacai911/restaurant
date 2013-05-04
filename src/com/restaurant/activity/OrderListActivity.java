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

		app = (TheApplication) getApplication(); 
		restid = app.getRestid();
		
		list = (ListView) findViewById(R.id.listView1);
		setTitle("订单一览");
		new Thread(progressThread).start();
		
		// 添加点击
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				HashMap<String, Object> thisorder = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
				int thisorderid = (Integer) thisorder.get("orderid");
				Intent intent = new Intent().setClass(OrderListActivity.this, OrderDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("orderid", thisorderid);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		
		
		
		
		this.mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// findViewById(R.id.progressBar1).setVisibility(View.GONE);
				if(msg.what==-123){
					Toast.makeText(OrderListActivity.this, "现无订单",Toast.LENGTH_SHORT).show();
				}else{
				listItemAdapter = new SimpleAdapter(OrderListActivity.this,
						(ArrayList<HashMap<String, Object>>) msg.obj,// 数据源
						R.layout.order_list_layout,// ListItem的XML实现
						// 动态数组与ImageItem对应的子项
						new String[] { "eattime", "maketime","delivery" },
						// ImageItem的XML文件里面的一个ImageView,两个TextView ID
						new int[] { R.id.textView4, R.id.textView2, R.id.textView5}

				);

				// 添加并且显示
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
		// 查询参数

		String registerString = "restid=" + id;
		// URL
		String url = HttpUtil.BASE_URL + "OrderListServlet?" + registerString;
		// 查询返回结果
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
				// map.put("ItemTitle", "Level "+i);
				// map.put("ItemText", "Finished in 1 Min 54 Secs, 70 Moves! ");
				// listItem.add(map);
				JSONObject order = (JSONObject) jsonArray.get(i);
				int orderid = order.getInt("orderid");
				boolean delivery = order.getBoolean("delivery");
				String eattime = order.getString("eattime");
				String maketime = order.getString("maketime");
				String theDelivery = "";
				if(delivery){
					theDelivery = "外送";
				}else{
					theDelivery = "来店";
				}
				map.put("orderid", orderid);
				map.put("delivery", theDelivery);
				map.put("eattime", eattime);
				map.put("maketime", maketime);
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
